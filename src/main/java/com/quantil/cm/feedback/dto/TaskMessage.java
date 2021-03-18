package com.quantil.cm.feedback.dto;

import com.alibaba.fastjson.JSON;
import com.quantil.cm.feedback.constant.Constants;
import org.apache.rocketmq.common.message.MessageExt;

public class TaskMessage {

    private String id;
    private int msgType;
    private int total;
    private int successCnt;
    private int failCnt;

    private boolean isPrefetch() {
        return this.msgType == Constants.MsgType.PREFETCH;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getSuccessCnt() {
        return successCnt;
    }

    public void setSuccessCnt(int successCnt) {
        this.successCnt = successCnt;
    }

    public int getFailCnt() {
        return failCnt;
    }

    public void setFailCnt(int failCnt) {
        this.failCnt = failCnt;
    }
}
