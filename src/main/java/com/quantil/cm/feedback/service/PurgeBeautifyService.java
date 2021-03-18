package com.quantil.cm.feedback.service;

import com.alibaba.fastjson.JSONObject;
import com.quantil.cm.feedback.constant.PurgeBeautifyProperties;
import com.quantil.cm.feedback.domain.PurgeTaskLog;
import com.quantil.cm.feedback.dto.TaskError;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * purge 查询美化类
 * 针对 FeedbackTimeout的errorCode, 视为成功.
 * 针对 AgentUnavailable的errorCode, 不计入统计
 */
@Service
public class PurgeBeautifyService {

    private static Logger logger = LoggerFactory.getLogger(PurgeBeautifyService.class);
    @Autowired
    private PurgeBeautifyProperties beautifyProperties;

    /**
     * 美化错误日志,根据规则移除logs中的部分错误,然后返回一个二维数组.第一个表示视为成功的log数量,第二个表示不计入统计的数量
     * @param errorLogs
     * @param totalCnt 总数
     * @return
     */
    public int[] beautifyPurgeLog(List<PurgeTaskLog> errorLogs, int totalCnt) {
        if (!beautifyProperties.isEnable() || errorLogs.isEmpty() || totalCnt <= 0){
            return new int[]{0,0};
        }
        int maxSkipErrorCodeCnt = beautifyProperties.getSkipThreshold().multiply(BigDecimal.valueOf(totalCnt)).setScale(0,BigDecimal.ROUND_UP).intValue();
        boolean reachSkipMaxRate = false;
        // 视为成功的log
        List<Integer> treatAsSuccessLogIndexList = new ArrayList<>();
        //视为失败的log
        List<Integer> skipLogIndexList = new ArrayList<>();
        for(int i=0;i<errorLogs.size();i++) {
            PurgeTaskLog log = errorLogs.get(i);
            if (StringUtils.isBlank(log.getReason())) {
                continue;
            }
            List<TaskError> errors = null;
            try {
                errors = JSONObject.parseArray(log.getReason(), TaskError.class);
            }catch (Exception e) {
                logger.error("parse reason to json array failed",e);
            }
            if (errors.size() != 1) {
                continue;//这里我们只考虑每台机器只有一个error(AgentUnavailable, FeedbackTimeout)的错误
            }
            String errorCode = errors.get(0).getErrorCode();
            if (beautifyProperties.getSuccessErrorCodes().indexOf(errorCode) >= 0) {
                treatAsSuccessLogIndexList.add(i);
            }else if (!reachSkipMaxRate && beautifyProperties.getSkipErrorCodes().indexOf(errorCode) >= 0) {
                skipLogIndexList.add(i);
                if (skipLogIndexList.size() >= maxSkipErrorCodeCnt) {
                    reachSkipMaxRate = true;
                    skipLogIndexList.clear();
                }
            }
        }
        int[] result = new int[]{treatAsSuccessLogIndexList.size(), skipLogIndexList.size()};
        treatAsSuccessLogIndexList.addAll(skipLogIndexList);
        List<Integer> deleteIndexList = treatAsSuccessLogIndexList.stream().distinct().sorted().collect(Collectors.toList());
        for (int i= deleteIndexList.size()-1;i>=0;i--) {
            errorLogs.remove(deleteIndexList.get(i).intValue());
        }
        return result;
    }
}
