package com.quantil.cm.feedback.dto;

import java.util.List;

public class PurgeFeedbackMessage extends PrefetchFeedbackMessage {

    private List<String> variedFiles;

    public PurgeFeedbackMessage(String id) {
        super(id);
    }

    public List<String> getVariedFiles() {
        return variedFiles;
    }

    public void setVariedFiles(List<String> variedFiles) {
        this.variedFiles = variedFiles;
    }
}
