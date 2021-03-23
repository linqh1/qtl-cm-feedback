package com.quantil.cm.feedback.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "cm.httpclient")
public class HttpClientProperties {

    /**
     * 是否处于调试模式. 是的话不会发起真正的http请求
     */
    private boolean debug = false;

    /**
     * http请求连接超时
     */
    private int connectTimeout = 10000;

    /**
     * http请求总超市
     */
    private int socketTimeout = 20000;

    private String address;

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
