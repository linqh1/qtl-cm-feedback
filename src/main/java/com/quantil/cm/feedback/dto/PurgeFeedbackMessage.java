package com.quantil.cm.feedback.dto;

import java.util.List;

public class PurgeFeedbackMessage extends PrefetchFeedbackMessage {

    private List<String> variedFiles;

    public PurgeFeedbackMessage(String id,int success, int total) {
        super(id,success,total);
    }

    public List<String> getVariedFiles() {
        return variedFiles;
    }

    public void setVariedFiles(List<String> variedFiles) {
        this.variedFiles = variedFiles;
    }
}
