package com.quantil.cm.feedback.constant;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "rocketmq.consumer")
public class RocketMQConsumerProperties {

    private String group;

    private String nameserver;

    private String topic;

    private int threads;

    private int messageBatchSize;

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getNameserver() {
        return nameserver;
    }

    public void setNameserver(String nameserver) {
        this.nameserver = nameserver;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public int getThreads() {
        return threads;
    }

    public void setThreads(int threads) {
        this.threads = threads;
    }

    public int getMessageBatchSize() {
        return messageBatchSize;
    }

    public void setMessageBatchSize(int messageBatchSize) {
        this.messageBatchSize = messageBatchSize;
    }
}
