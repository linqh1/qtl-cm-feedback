package com.quantil.cm.feedback.service;

import com.alibaba.fastjson.JSON;
import com.quantil.cm.feedback.domain.PurgeTaskLog;
import com.quantil.cm.feedback.dto.PrefetchFeedbackMessage;
import com.quantil.cm.feedback.dto.TaskError;
import com.quantil.cm.feedback.dto.TaskMessage;
import com.quantil.cm.feedback.mapper.PrefetchTaskLogMapper;
import com.quantil.cm.feedback.mapper.PurgeTaskLogMapper;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
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
import java.util.stream.IntStream;

@Service
public class MessageHandler implements MessageListenerConcurrently {

    private static Logger logger = LoggerFactory.getLogger(MessageHandler.class);

    @Value("${httpclient.timeout.connect:10000}")
    private int connTimeout;
    @Value("${httpclient.timeout.socket:30000}")
    private int socketTimeout;
    @Autowired
    private PurgeTaskLogMapper purgeTaskLogMapper;
    @Autowired
    private PrefetchTaskLogMapper prefetchTaskLogMapper;

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
        List<TaskMessage> messages = parseTaskMessage(msgs);
        send(messages);
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }

    /**
     * 解析消息
     * @param msgs
     * @return
     */
    private List<TaskMessage> parseTaskMessage(List<MessageExt> msgs) {
        List<TaskMessage> taskMessages = new ArrayList<>();
        for (MessageExt msg : msgs) {
            try {
                taskMessages.add(JSON.parseObject(msg.getBody(),TaskMessage.class));
            }catch (Exception e){// 解析失败就不回推MQ了, 回推了重新拉还是解析失败
                logger.error("parse MQ message:{} failed:{}",new String(msg.getBody()),e);
            }
        }
        return taskMessages;
    }

    /**
     * 发送消息
     * @param messages
     */
    private void send(List<TaskMessage> messages) {
        //TODO send to NGAPI
        List<TaskMessage> prefetchMessage = messages.stream().filter(m -> m.isPrefetch()).collect(Collectors.toList());
        List<TaskMessage> purgeMessage = messages.stream().filter(m -> !m.isPrefetch()).collect(Collectors.toList());

        List<PrefetchFeedbackMessage> prefetchFeedbackMessageList = getPrefetchFeedbackMessage(prefetchMessage);
//        List<PurgeFeedbackMessage> purgeFeedbackMessageList = getPurgeFeedbackMessage(purgeMessage);
    }

    /**
     * 获取prefetch反馈消息
     * @param prefetchMessage
     * @return
     */
    private List<PrefetchFeedbackMessage> getPrefetchFeedbackMessage(List<TaskMessage> prefetchMessage) {
        List<PrefetchFeedbackMessage> result = new ArrayList<>();
        // 失败数为0的任务
        List<TaskMessage> nonFailedTask = prefetchMessage.stream().filter(m -> m.getFailCnt() <= 0).collect(Collectors.toList());
        nonFailedTask.forEach(t -> result.add(new PrefetchFeedbackMessage(t.getTaskId(),t.getSuccessCnt(),t.getTotal())));
        // 失败数不为0的任务
        List<TaskMessage> failedTask = prefetchMessage.stream().filter(m -> m.getFailCnt() > 0).collect(Collectors.toList());
        if (!failedTask.isEmpty()) {
            List<PurgeTaskLog> purgeTaskLogs = prefetchTaskLogMapper.selectByTaskId(failedTask.stream().map(TaskMessage::getTaskId).collect(Collectors.toList()));
            Map<String, List<PurgeTaskLog>> purgeTaskLogMap = purgeTaskLogs.stream().collect(Collectors.groupingBy(PurgeTaskLog::getPurgeTaskId));
            for (TaskMessage message : failedTask) {
                PrefetchFeedbackMessage feedbackMessage = new PrefetchFeedbackMessage(message.getTaskId(),message.getSuccessCnt(),message.getTotal());
                feedbackMessage.setMessage(generateErrorMessage(purgeTaskLogMap.get(message.getTaskId())));
                result.add(feedbackMessage);
            }
        }
        return result;
    }

    /**
     * 合并错误日志为一个字符串
     * @param purgeTaskLogs
     * @return
     */
    private String generateErrorMessage(List<PurgeTaskLog> purgeTaskLogs) {
        if (purgeTaskLogs == null || purgeTaskLogs.isEmpty()) {
            return null;
        }
        List<TaskError> taskErrorList = purgeTaskLogs.stream()
                .map(log -> JSON.parseArray(log.getReason(), TaskError.class))
                .collect(ArrayList::new, ArrayList::addAll, (left, right) -> left.addAll(right));
        Map<String, List<TaskError>> errorSummary = taskErrorList.stream().collect(Collectors.groupingBy(TaskError::getErrorCode));
        return errorSummary.keySet().stream()
                .sorted() // key排序
                .map(errorCode -> errorCode + " x" + errorSummary.get(errorCode).size()) // 每个error code的错误信息格式: %v xcnt
                .collect(Collectors.joining(", "));
    }

    public static void main(String[] main) throws Exception {
        MessageHandler messageHandler = new MessageHandler();
        List<String> errorCodes = Arrays.asList(new String[]{"BuildRequestFailed","PurgeFailed","PurgeTimeout","VariedFile","UnexpectedStatusCode"});
        int size = errorCodes.size();
        List<PurgeTaskLog> logs = IntStream.range(0, 100).mapToObj(i -> {
            PurgeTaskLog log = new PurgeTaskLog();
            log.setReason(JSON.toJSONString(Arrays.asList(new TaskError(errorCodes.get(new Random().nextInt(size))))));
            return log;
        }).collect(Collectors.toList());
        System.out.println(messageHandler.generateErrorMessage(logs));
    }
}
