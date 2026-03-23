package com.example.healthcard_demo;

import java.util.List;

public class LocalDiseasePredictor {

    public DiseaseResponse predict(List<Float> features) {
        DiseaseResponse response = new DiseaseResponse();

        if (features == null || features.size() < SymptomPreprocessor.FEATURE_SIZE) {
            response.setPredictedDisease("General Viral Infection");
            response.setConfidence(0.55f);
            response.setSeverity("Mild");
            return response;
        }

        float fever = features.get(0);
        float cough = features.get(1);
        float headache = features.get(2);
        float fatigue = features.get(3);
        float vomiting = features.get(4);
        float chestPain = features.get(5);
        float nausea = features.get(8);
        float breathlessness = features.get(9);
        float severitySignal = features.get(10);

        String predicted;
        float confidence;

        if (fever == 1f && vomiting == 1f && nausea == 1f) {
            predicted = "Dengue";
            confidence = 0.84f;
        } else if (cough == 1f && chestPain == 1f && breathlessness == 1f) {
            predicted = "Bronchitis";
            confidence = 0.81f;
        } else if (headache == 1f && fatigue == 1f && fever == 0f) {
            predicted = "Migraine";
            confidence = 0.79f;
        } else if (fever == 1f && cough == 1f) {
            predicted = "Flu";
            confidence = 0.77f;
        } else {
            predicted = "General Viral Infection";
            confidence = 0.62f;
        }

        response.setPredictedDisease(predicted);
        response.setConfidence(confidence);

        if (severitySignal >= 0.9f) {
            response.setSeverity("Severe");
        } else if (severitySignal >= 0.6f) {
            response.setSeverity("Moderate");
        } else {
            response.setSeverity("Mild");
        }

        return response;
    }
}
