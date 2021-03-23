package com.quantil.cm.feedback.service;

import com.quantil.cm.feedback.domain.PurgeTaskLog;
import com.quantil.cm.feedback.domain.PurgeVaryUrl;
import com.quantil.cm.feedback.dto.PurgeFeedbackMessage;
import com.quantil.cm.feedback.dto.TaskMessage;
import com.quantil.cm.feedback.mapper.PurgeTaskLogMapper;
import com.quantil.cm.feedback.mapper.PurgeVaryUrlMapper;
import com.quantil.cm.feedback.util.MessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PurgeService {

    @Autowired
    BeautifyService beautifyService;
    @Autowired
    PurgeTaskLogMapper purgeTaskLogMapper;
    @Autowired
    PurgeVaryUrlMapper purgeVaryUrlMapper;

    /**
     * 获取purge反馈消息
     * @param purgeMessage
     * @return
     */
    public List<PurgeFeedbackMessage> getFeedbackMessage(List<TaskMessage> purgeMessage) {
        List<PurgeFeedbackMessage> result = new ArrayList<>();
        // 失败数为0的任务
        List<TaskMessage> nonFailedTask = purgeMessage.stream().filter(m -> m.getFailCnt() <= 0).collect(Collectors.toList());
        nonFailedTask.forEach(t -> result.add(new PurgeFeedbackMessage(t.getTaskId(),t.getSuccessCnt(),t.getTotal())));
        // 失败数不为0的任务
        List<TaskMessage> failedTask = purgeMessage.stream().filter(m -> m.getFailCnt() > 0).collect(Collectors.toList());
        if (!failedTask.isEmpty()) {
            List<String> failedIdList = failedTask.stream().map(TaskMessage::getTaskId).collect(Collectors.toList());
            List<PurgeTaskLog> purgeTaskLogs = purgeTaskLogMapper.selectByTaskId(failedIdList);
            Map<String, List<PurgeTaskLog>> purgeTaskLogMap = purgeTaskLogs.stream().collect(Collectors.groupingBy(PurgeTaskLog::getPurgeTaskId));
            for (TaskMessage message : failedTask) {
                List<PurgeTaskLog> logs = purgeTaskLogMap.get(message.getTaskId());
                int[] res = beautifyService.beautifyPurgeLog(logs,message.getTotal());
                PurgeFeedbackMessage feedbackMessage = new PurgeFeedbackMessage(message.getTaskId(),
                        message.getSuccessCnt() + res[0],message.getTotal() - res[1]);
                feedbackMessage.setMessage(MessageUtil.logSummary(logs));
                feedbackMessage.setVariedFiles(null);
                result.add(feedbackMessage);
            }
        }
        // 设置Vary File
        List<String> purgeIdList = purgeMessage.stream().map(m -> m.getTaskId()).collect(Collectors.toList());
        List<PurgeVaryUrl> purgeVaryUrlList = purgeVaryUrlMapper.selectByTaskId(purgeIdList);
        Map<String, List<PurgeVaryUrl>> varyUrlMap = purgeVaryUrlList.stream().collect(Collectors.groupingBy(PurgeVaryUrl::getTaskId));
        for (PurgeFeedbackMessage feedbackMessage : result) {
            List<PurgeVaryUrl> urlList = varyUrlMap.get(feedbackMessage.getId());
            if (urlList != null) {
                feedbackMessage.setVariedFiles(urlList.stream().map(url -> url.getUrl()).distinct().collect(Collectors.toList()));
            }
        }
        return result;
    }
}
