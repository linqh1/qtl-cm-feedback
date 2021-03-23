package com.quantil.cm.feedback.dto;

public class MQMessage {

    private String taskId;
    /**
     * 0: purge 1:prefetch
     */
    private int msgType;
    private int total;
    private int successCnt;
    private int failCnt;

    /**
     * 是否是一个预取消息
     * @return
     */
    public boolean isPrefetch() {
        return this.msgType ==0;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
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

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }
}
