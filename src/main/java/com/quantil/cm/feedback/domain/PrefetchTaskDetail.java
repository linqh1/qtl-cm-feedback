package com.quantil.cm.feedback.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;

import java.util.Date;

@TableName("prefetch_task_detail")
public class PrefetchTaskDetail {
    private String id;

    @TableField("prefetch_task_id")
    private String prefetchTaskId;

    private String url;

    private String domain;

    private String headers;

    /**
     * Prefetch strategy, 0:edge  1:parent, default is 1
     */
    private int strategy;

    private Date commitTime;

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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getHeaders() {
        return headers;
    }

    public void setHeaders(String headers) {
        this.headers = headers;
    }

    public int getStrategy() {
        return strategy;
    }

    public void setStrategy(int strategy) {
        this.strategy = strategy;
    }

    public Date getCommitTime() {
        return commitTime;
    }

    public void setCommitTime(Date commitTime) {
        this.commitTime = commitTime;
    }
}
