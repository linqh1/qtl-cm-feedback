package com.quantil.cm.feedback.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;

import java.util.Date;

@TableName("purge_task_detail")
public class PurgeTaskDetail {
    private String id;

    private String url;

    private String domain;

    @TableField("purge_task_id")
    private String purgeTaskId;

    private String headers;

    private String type;

    private Date commitTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPurgeTaskId() {
        return purgeTaskId;
    }

    public void setPurgeTaskId(String purgeTaskId) {
        this.purgeTaskId = purgeTaskId;
    }

    public String getHeaders() {
        return headers;
    }

    public void setHeaders(String headers) {
        this.headers = headers;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getCommitTime() {
        return commitTime;
    }

    public void setCommitTime(Date commitTime) {
        this.commitTime = commitTime;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }


}
