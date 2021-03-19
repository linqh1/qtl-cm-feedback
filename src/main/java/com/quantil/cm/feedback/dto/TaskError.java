package com.quantil.cm.feedback.dto;

public class TaskError {

    public TaskError() {}

    public TaskError(String errorCode) {
        this.errorCode = errorCode;
    }

    private String errorCode;

    private String errorMessage;

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

}
