package com.example.healthcard_demo;

public class PredictionHistoryItem {
    private String disease;
    private String severity;
    private float confidence;
    private long timestamp;

    public PredictionHistoryItem(String disease, String severity, float confidence, long timestamp) {
        this.disease = disease;
        this.severity = severity;
        this.confidence = confidence;
        this.timestamp = timestamp;
    }

    public String getDisease() {
        return disease;
    }

    public String getSeverity() {
        return severity;
    }

    public float getConfidence() {
        return confidence;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
