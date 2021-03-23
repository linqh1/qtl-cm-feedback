package com.quantil.cm.feedback.dto;

import java.util.List;

public class PurgeMessage extends PrefetchMessage {

    private List<String> variedFiles;

    public PurgeMessage(String id, int success, int total) {
        super(id,success,total);
    }

    public List<String> getVariedFiles() {
        return variedFiles;
    }

    public void setVariedFiles(List<String> variedFiles) {
        this.variedFiles = variedFiles;
    }
}
