package com.example.healthcard_demo;

public class PredictionHistoryItem {
    private String disease;
    private String severity;
    private int severityScore;
    private String description;
    private long timestamp;

    public PredictionHistoryItem() {
    }

    public PredictionHistoryItem(String disease, String severity, int severityScore, String description, long timestamp) {
        this.disease = disease;
        this.severity = severity;
        this.severityScore = severityScore;
        this.description = description;
        this.timestamp = timestamp;
    }

    public String getDisease() {
        return disease;
    }

    public String getSeverity() {
        return severity;
    }

    public int getSeverityScore() {
        return severityScore;
    }

    public String getDescription() {
        return description;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
