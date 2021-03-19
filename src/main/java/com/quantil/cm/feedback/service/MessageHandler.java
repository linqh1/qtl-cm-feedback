package com.quantil.cm.feedback.service;

import com.alibaba.fastjson.JSON;
import com.quantil.cm.feedback.dto.PrefetchFeedbackMessage;
import com.quantil.cm.feedback.dto.PurgeFeedbackMessage;
import com.quantil.cm.feedback.dto.TaskMessage;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageHandler implements MessageListenerConcurrently {

    private static Logger logger = LoggerFactory.getLogger(MessageHandler.class);

    @Value("${cm.feedback.timeout.connect:10000}")
    private int connTimeout;
    @Value("${cm.feedback.timeout.socket:20000}")
    private int socketTimeout;

    @Value("cm.feedback.addr")
    private String httpAddr;

    @Autowired
    private PurgeService purgeService;
    @Autowired
    private PrefetchService prefetchService;

    private HttpClient httpClient = null;
    private PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();

    @PostConstruct
    public void init() {
        httpClient = HttpClients.custom()
                .useSystemProperties()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(connTimeout)
                        .setSocketTimeout(socketTimeout)
                        .build())
                .setConnectionManager(connectionManager)
                .build();
    }

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        logger.info("consume {} message", msgs.size());
        List<HttpPut> requests = trans2Request(msgs);
        try {
            for (HttpPut request : requests) {
                httpClient.execute(request);
            }
        }catch (Exception e) {
            logger.error("execute http request failed", e);
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }

    /**
     * 将消息转化成HTTP请求
     * @param msgs
     * @return
     */
    private List<HttpPut> trans2Request(List<MessageExt> msgs) {
        List<TaskMessage> taskMessages = new ArrayList<>();
        for (MessageExt msg : msgs) {
            try {
                logger.debug("consume msg: {}", new String(msg.getBody()));
                taskMessages.add(JSON.parseObject(msg.getBody(),TaskMessage.class));
            }catch (Exception e){// 解析失败就不回推MQ了, 回推了重新拉还是解析失败
                logger.error("parse MQ message:{} failed:{}",new String(msg.getBody()),e);
            }
        }
        List<HttpPut> result = new ArrayList<>();

        List<TaskMessage> prefetchMessage = taskMessages.stream().filter(m -> m.isPrefetch()).collect(Collectors.toList());
        List<PrefetchFeedbackMessage> prefetchFeedbackList = prefetchService.getFeedbackMessage(prefetchMessage);
        if (!prefetchFeedbackList.isEmpty()) {
            result.add(buildHttpRequest("/internal/prefetches",new StringEntity(JSON.toJSONString(prefetchFeedbackList),"UTF-8")));
        }

        List<TaskMessage> purgeMessage = taskMessages.stream().filter(m -> !m.isPrefetch()).collect(Collectors.toList());
        List<PurgeFeedbackMessage> purgeFeedbackList = purgeService.getFeedbackMessage(purgeMessage);
        if (!purgeFeedbackList.isEmpty()) {
            result.add(buildHttpRequest("/internal/purges",new StringEntity(JSON.toJSONString(purgeFeedbackList),"UTF-8")));
        }
        return result;
    }

    private HttpPut buildHttpRequest(String api, HttpEntity entity) {
        HttpPut httpPut = new HttpPut(httpAddr + api);
        httpPut.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        httpPut.setEntity(entity);
        return httpPut;
    }
}
