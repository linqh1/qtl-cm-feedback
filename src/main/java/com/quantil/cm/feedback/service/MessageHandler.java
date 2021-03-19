package com.quantil.cm.feedback.service;

import com.alibaba.fastjson.JSON;
import com.quantil.cm.feedback.dto.PrefetchFeedbackMessage;
import com.quantil.cm.feedback.dto.PurgeFeedbackMessage;
import com.quantil.cm.feedback.dto.TaskMessage;
import org.apache.http.HttpRequest;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
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
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MessageHandler implements MessageListenerConcurrently {

    private static Logger logger = LoggerFactory.getLogger(MessageHandler.class);

    @Value("${httpclient.timeout.connect:10000}")
    private int connTimeout;
    @Value("${httpclient.timeout.socket:20000}")
    private int socketTimeout;

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
        List<HttpRequest> requests = trans2Request(msgs);
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }

    /**
     * 将消息转化成HTTP请求
     * @param msgs
     * @return
     */
    private List<HttpRequest> trans2Request(List<MessageExt> msgs) {
        List<TaskMessage> taskMessages = new ArrayList<>();
        for (MessageExt msg : msgs) {
            try {
                taskMessages.add(JSON.parseObject(msg.getBody(),TaskMessage.class));
            }catch (Exception e){// 解析失败就不回推MQ了, 回推了重新拉还是解析失败
                logger.error("parse MQ message:{} failed:{}",new String(msg.getBody()),e);
            }
        }
        List<HttpRequest> result = new ArrayList<>();
        List<TaskMessage> prefetchMessage = taskMessages.stream().filter(m -> m.isPrefetch()).collect(Collectors.toList());
        List<PrefetchFeedbackMessage> prefetchFeedbackList = prefetchService.getFeedbackMessage(prefetchMessage);
        if (!prefetchFeedbackList.isEmpty()) {
            HttpPost httpPost = new HttpPost();
        }

        List<TaskMessage> purgeMessage = taskMessages.stream().filter(m -> !m.isPrefetch()).collect(Collectors.toList());
        List<PurgeFeedbackMessage> purgeFeedbackList = purgeService.getFeedbackMessage(purgeMessage);
        return result;
    }
}
