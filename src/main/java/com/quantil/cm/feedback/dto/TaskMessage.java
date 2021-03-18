package com.quantil.cm.feedback.dto;

public class TaskMessage {

    private String taskId;
    private String type;
    private int total;
    private int successCnt;
    private int failCnt;

    /**
     * 是否是一个预取消息
     * @return
     */
    public boolean isPrefetch() {
        return this.type != null && this.type.equalsIgnoreCase("prefetch");
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
