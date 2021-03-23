package com.quantil.cm.feedback.service;

import com.alibaba.fastjson.JSON;
import com.quantil.cm.feedback.properties.MQConsumerProperties;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * RocketMQ 消费者配置
 */
@Service
public class MessageConsumer {

    private static Logger logger = LoggerFactory.getLogger(MessageHandler.class);

    private DefaultMQPushConsumer pushConsumer = null;

    @Autowired
    private MQConsumerProperties consumerProperties;

    @Autowired
    private MessageHandler messageHandler;

    @PostConstruct
    public void init() throws MQClientException, InterruptedException {
        logger.info("rocketmq config: {}", JSON.toJSONString(consumerProperties));
        pushConsumer = new DefaultMQPushConsumer(consumerProperties.getGroup());
        pushConsumer.setNamesrvAddr(consumerProperties.getNameserver());
        pushConsumer.subscribe(consumerProperties.getTopic(),"*");
        pushConsumer.setConsumeThreadMax(consumerProperties.getThreads());
        pushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        pushConsumer.setConsumeMessageBatchMaxSize(consumerProperties.getMessageBatchSize());
        pushConsumer.registerMessageListener(messageHandler);
        pushConsumer.start();
    }
    
    @PreDestroy
    public void destroy() {
        logger.info("close rocketmq client");
        pushConsumer.shutdown();
    }

}
