package com.example.healthcard_demo;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class DiseaseResponse {

    @SerializedName(value = "predictedDisease", alternate = {"predicted_disease", "disease"})
    private String predictedDisease;

    @SerializedName(value = "confidence", alternate = {"confidence_score", "score"})
    private float confidence;

    @SerializedName(value = "severity", alternate = {"severity_level", "risk"})
    private String severity;

    private int severityScore;
    private String description;
    private List<String> precautions = new ArrayList<>();
    private String doctorDetails;
    private String dietPlan;
    private String exercise;
    private long predictionTimestamp;
    private String inputSeverity;

    public String getPredictedDisease() {
        return predictedDisease;
    }

    public void setPredictedDisease(String predictedDisease) {
        this.predictedDisease = predictedDisease;
    }

    public float getConfidence() {
        return confidence;
    }

    public void setConfidence(float confidence) {
        this.confidence = confidence;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public int getSeverityScore() {
        return severityScore;
    }

    public void setSeverityScore(int severityScore) {
        this.severityScore = severityScore;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getPrecautions() {
        return precautions;
    }

    public void setPrecautions(List<String> precautions) {
        this.precautions = precautions;
    }

    public String getDoctorDetails() {
        return doctorDetails;
    }

    public void setDoctorDetails(String doctorDetails) {
        this.doctorDetails = doctorDetails;
    }

    public String getDietPlan() {
        return dietPlan;
    }

    public void setDietPlan(String dietPlan) {
        this.dietPlan = dietPlan;
    }

    public String getExercise() {
        return exercise;
    }

    public void setExercise(String exercise) {
        this.exercise = exercise;
    }

    public long getPredictionTimestamp() {
        return predictionTimestamp;
    }

    public void setPredictionTimestamp(long predictionTimestamp) {
        this.predictionTimestamp = predictionTimestamp;
    }

    public String getInputSeverity() {
        return inputSeverity;
    }

    public void setInputSeverity(String inputSeverity) {
        this.inputSeverity = inputSeverity;
    }
}
