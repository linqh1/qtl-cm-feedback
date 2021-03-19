package com.quantil.cm.feedback.constant;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "cm.beautify")
public class BeautifyProperties {

    /**
     * 是否开启美化
     */
    private boolean enable;

    /**
     * 视为成功的error code
     */
    private List<String> successErrorCodes;

    /**
     * 不计入统计的error code
     */
    private List<String> skipErrorCodes;

    /**
     * 当不计入统计的error code低于该比率时， 才真的不计入统计
     */
    private BigDecimal skipThreshold;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public List<String> getSuccessErrorCodes() {
        return successErrorCodes;
    }

    public void setSuccessErrorCodes(List<String> successErrorCodes) {
        this.successErrorCodes = successErrorCodes;
    }

    public List<String> getSkipErrorCodes() {
        return skipErrorCodes;
    }

    public void setSkipErrorCodes(List<String> skipErrorCodes) {
        this.skipErrorCodes = skipErrorCodes;
    }

    public BigDecimal getSkipThreshold() {
        return skipThreshold;
    }

    public void setSkipThreshold(BigDecimal skipThreshold) {
        this.skipThreshold = skipThreshold;
    }
}
