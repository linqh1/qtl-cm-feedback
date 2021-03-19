package com.quantil.cm.feedback.util;

import com.alibaba.fastjson.JSON;
import com.quantil.cm.feedback.domain.PurgeTaskLog;
import com.quantil.cm.feedback.domain.TaskLog;
import com.quantil.cm.feedback.dto.TaskError;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MessageUtil {

    /**
     * 合并错误日志为一个字符串
     * @param taskLogs
     * @return
     */
    public static String logSummary(List<? extends TaskLog> taskLogs) {
        if (taskLogs == null || taskLogs.isEmpty()) {
            return null;
        }
        List<TaskError> taskErrorList = taskLogs.stream()
                .map(log -> JSON.parseArray(log.getReason(), TaskError.class))
                .collect(ArrayList::new, ArrayList::addAll, (left, right) -> left.addAll(right));
        Map<String, List<TaskError>> errorSummary = taskErrorList.stream().collect(Collectors.groupingBy(TaskError::getErrorCode));
        return errorSummary.keySet().stream()
                .sorted() // key排序
                .map(errorCode -> errorCode + " x" + errorSummary.get(errorCode).size()) // 每个error code的错误信息格式: %v xcnt
                .collect(Collectors.joining(", "));
    }

    public static void main(String[] main) throws Exception {
        List<String> errorCodes = Arrays.asList(new String[]{"BuildRequestFailed","PurgeFailed","PurgeTimeout","VariedFile","UnexpectedStatusCode"});
        int size = errorCodes.size();
        List<PurgeTaskLog> logs = IntStream.range(0, 100).mapToObj(i -> {
            PurgeTaskLog log = new PurgeTaskLog();
            log.setReason(JSON.toJSONString(Arrays.asList(new TaskError(errorCodes.get(new Random().nextInt(size))))));
            return log;
        }).collect(Collectors.toList());
        System.out.println(logSummary(logs));
    }
}
