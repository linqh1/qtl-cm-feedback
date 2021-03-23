package com.quantil.cm.feedback.service;

import com.quantil.cm.feedback.domain.PrefetchTaskLog;
import com.quantil.cm.feedback.dto.PrefetchMessage;
import com.quantil.cm.feedback.dto.MQMessage;
import com.quantil.cm.feedback.mapper.PrefetchTaskLogMapper;
import com.quantil.cm.feedback.util.MessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PrefetchService {
    @Autowired
    private PrefetchTaskLogMapper prefetchTaskLogMapper;

    /**
     * 获取prefetch反馈消息
     * @param prefetchMessage
     * @return
     */
    public List<PrefetchMessage> getFeedbackMessage(List<MQMessage> prefetchMessage) {
        List<PrefetchMessage> result = new ArrayList<>();
        // 失败数为0的任务
        List<MQMessage> nonFailedTask = prefetchMessage.stream().filter(m -> m.getFailCnt() <= 0).collect(Collectors.toList());
        nonFailedTask.forEach(t -> result.add(new PrefetchMessage(t.getTaskId(),t.getSuccessCnt(),t.getTotal())));
        // 失败数不为0的任务
        List<MQMessage> failedTask = prefetchMessage.stream().filter(m -> m.getFailCnt() > 0).collect(Collectors.toList());
        if (!failedTask.isEmpty()) {
            List<String> failedIdList = failedTask.stream().map(MQMessage::getTaskId).collect(Collectors.toList());
            List<PrefetchTaskLog> prefetchTaskLogs = prefetchTaskLogMapper.selectByTaskId(failedIdList);
            Map<String, List<PrefetchTaskLog>> prefetchTaskLogMap = prefetchTaskLogs.stream().collect(Collectors.groupingBy(PrefetchTaskLog::getPrefetchTaskId));
            for (MQMessage message : failedTask) {
                PrefetchMessage feedbackMessage = new PrefetchMessage(message.getTaskId(),message.getSuccessCnt(),message.getTotal());
                feedbackMessage.setMessage(MessageUtil.logSummary(prefetchTaskLogMap.get(message.getTaskId())));
                result.add(feedbackMessage);
            }
        }
        return result;
    }
}
