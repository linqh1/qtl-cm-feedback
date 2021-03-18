package com.quantil.cm.feedback.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;

import java.util.Date;

@TableName("purge_task")
public class PurgeTask {

    private String id;

    private String name;

    private String target;

    private String action;

    @TableField("customer_id")
    private String customerId;

    @TableField("customer_name")
    private String customerName;

    @TableField("api_request_id")
    private String apiRequestId;

    @TableField("api_account_id")
    private Integer apiAccountId;

    private String status;

    @TableField("total_cnt")
    private int totalCnt;

    @TableField("success_cnt")
    private int successCnt;

    @TableField("fail_cnt")
    private int failCnt;

    @TableField("commit_time")
    private Date commitTime;

    @TableField("finish_time")
    private Date finishTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTotalCnt() {
        return totalCnt;
    }

    public void setTotalCnt(int totalCnt) {
        this.totalCnt = totalCnt;
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

    public Date getCommitTime() {
        return commitTime;
    }

    public void setCommitTime(Date commitTime) {
        this.commitTime = commitTime;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }

    public String getApiRequestId() {
        return apiRequestId;
    }

    public void setApiRequestId(String apiRequestId) {
        this.apiRequestId = apiRequestId;
    }

    public Integer getApiAccountId() {
        return apiAccountId;
    }

    public void setApiAccountId(Integer apiAccountId) {
        this.apiAccountId = apiAccountId;
    }
}
