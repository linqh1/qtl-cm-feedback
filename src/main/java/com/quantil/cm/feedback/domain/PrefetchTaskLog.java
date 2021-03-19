package com.quantil.cm.feedback.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;

import java.util.Date;

@TableName("prefetch_task_log")
public class PrefetchTaskLog implements TaskLog {

    private String id;

    @TableField("prefetch_task_id")
    private String prefetchTaskId;

    @TableField("cache_group_name")
    private String cacheGroupName;

    @TableField("cache_group_id")
    private String cacheGroupId;

    private String state;

    private String reason;

    @TableField("created_time ")
    private Date createdTime;

    @TableField("updated_time")
    private Date updatedTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPrefetchTaskId() {
        return prefetchTaskId;
    }

    public void setPrefetchTaskId(String prefetchTaskId) {
        this.prefetchTaskId = prefetchTaskId;
    }

    public String getCacheGroupName() {
        return cacheGroupName;
    }

    public void setCacheGroupName(String cacheGroupName) {
        this.cacheGroupName = cacheGroupName;
    }

    public String getCacheGroupId() {
        return cacheGroupId;
    }

    public void setCacheGroupId(String cacheGroupId) {
        this.cacheGroupId = cacheGroupId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }
}
