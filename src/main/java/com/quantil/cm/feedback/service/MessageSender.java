package com.quantil.cm.feedback.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.util.IOUtils;
import com.quantil.cm.feedback.dto.MQMessage;
import com.quantil.cm.feedback.dto.PrefetchMessage;
import com.quantil.cm.feedback.dto.PurgeMessage;
import com.quantil.cm.feedback.properties.HttpClientProperties;
import com.quantil.cm.feedback.util.EncryptUtil;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
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
import java.util.Map;
import java.util.stream.Collectors;

/**
 * RocketMQ 消息处理类
 */
@Service
public class MessageSender implements MessageListenerConcurrently {

    private static Logger logger = LoggerFactory.getLogger(MessageSender.class);

    @Autowired
    HttpClientProperties clientProperties;
    @Autowired
    private PurgeService purgeService;
    @Autowired
    private PrefetchService prefetchService;
    @Autowired
    private AlertService alertService;
    @Value("${ngapi.user}")
    private String ngapiUser;
    @Value("${ngapi.password}")
    private String ngapiPassword;

    private CloseableHttpClient httpClient = null;
    private PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();

    @PostConstruct
    public void init() {
        logger.info("httpclient config:{}",JSON.toJSONString(clientProperties));
        httpClient = HttpClients.custom()
                .useSystemProperties()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(clientProperties.getConnectTimeout())
                        .setSocketTimeout(clientProperties.getSocketTimeout())
                        .build())
                .setConnectionManager(connectionManager)
                .build();
    }

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        logger.info("consume {} message", msgs.size());
        List<HttpPut> requests = trans2Request(msgs);
        if (clientProperties.isDebug()) {
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
        List<CloseableHttpResponse> responseList = new ArrayList<>();
        try {
            for (HttpPut request : requests) {
                CloseableHttpResponse response = httpClient.execute(request);
                responseList.add(response);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode/100 != 2) {
                    logger.error("feedback return non-2xx status code:{}. body: {}", statusCode,
                            EntityUtils.toString(response.getEntity()));
                    alertService.alert("Param=SendFailed-" + statusCode);
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
            }
        }catch (Exception e) {
            logger.error("execute http request failed", e);
            alertService.alert("Param=SendFailed");
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }finally {
            responseList.forEach(resp -> {
                EntityUtils.consumeQuietly(resp.getEntity());
                IOUtils.close(resp);
            });
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }

    /**
     * 将消息转化成HTTP请求
     * @param msgs
     * @return
     */
    private List<HttpPut> trans2Request(List<MessageExt> msgs) {
        List<MQMessage> mqMessages = new ArrayList<>();
        for (MessageExt msg : msgs) {
            try {
                mqMessages.addAll(JSON.parseArray(new String(msg.getBody()), MQMessage.class));
            }catch (Exception e){// 解析失败就不回推MQ了, 回推了重新拉还是解析失败
                logger.error("parse MQ message:{} failed:{}",new String(msg.getBody()),e);
                alertService.alert("Param=UnrecognizedMessage");
            }
        }
        logger.debug("consumer message:{}",JSON.toJSONString(mqMessages));
        List<HttpPut> result = new ArrayList<>();

        List<MQMessage> prefetchMessage = mqMessages.stream().filter(m -> m.isPrefetch()).collect(Collectors.toList());
        if (!prefetchMessage.isEmpty()) {
            List<PrefetchMessage> prefetchFeedbackList = prefetchService.getFeedbackMessage(prefetchMessage);
            if (!prefetchFeedbackList.isEmpty()) {
                String prefetchBody = JSON.toJSONString(prefetchFeedbackList);
                logger.debug("prefetch feedback body:{}",prefetchBody);
                result.add(buildHttpRequest("/internal/prefetches",new StringEntity(prefetchBody,"UTF-8")));
            }
        }

        List<MQMessage> purgeMessage = mqMessages.stream().filter(m -> !m.isPrefetch()).collect(Collectors.toList());
        if (!purgeMessage.isEmpty()) {
            List<PurgeMessage> purgeFeedbackList = purgeService.getFeedbackMessage(purgeMessage);
            if (!purgeFeedbackList.isEmpty()) {
                String purgeBody = JSON.toJSONString(purgeFeedbackList);
                logger.debug("purge feedback body:{}",purgeBody);
                result.add(buildHttpRequest("/internal/purges",new StringEntity(purgeBody,"UTF-8")));
            }
        }
        return result;
    }

    /**
     * 构建HTTP请求
     * @param api
     * @param entity
     * @return
     */
    private HttpPut buildHttpRequest(String api, HttpEntity entity) {
        HttpPut httpPut = new HttpPut(clientProperties.getAddress() + api);
        httpPut.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        httpPut.setEntity(entity);
        Map<String, String> authHeader = EncryptUtil.quantilAuthHeader(ngapiUser, ngapiPassword);
        authHeader.forEach((k,v) -> httpPut.setHeader(k,v));
        return httpPut;
    }
}
