package com.quantil.cm.feedback.dto;

import java.math.BigDecimal;

public class PrefetchFeedbackMessage {
    private String id;
    private BigDecimal successRate;
    private String message;

    public PrefetchFeedbackMessage(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public BigDecimal getSuccessRate() {
        return successRate;
    }

    public void setSuccessRate(BigDecimal successRate) {
        this.successRate = successRate;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
