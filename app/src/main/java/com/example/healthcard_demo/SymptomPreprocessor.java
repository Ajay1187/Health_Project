package com.example.healthcard_demo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class SymptomPreprocessor {

    public static final int FEATURE_SIZE = 12;

    private static final String[] SUPPORTED_SYMPTOMS = new String[]{
            "fever", "cough", "headache", "fatigue", "vomiting",
            "chest pain", "sore throat", "body pain", "nausea", "breathlessness"
    };

    public List<Float> toFeatureVector(List<String> symptoms, String severity, int durationDays) {
        List<Float> vector = new ArrayList<>();
        Set<String> cleanedSymptoms = cleanSymptoms(symptoms);

        for (String knownSymptom : SUPPORTED_SYMPTOMS) {
            vector.add(cleanedSymptoms.contains(knownSymptom) ? 1f : 0f);
        }

        vector.add(normalizeSeverity(severity));
        int safeDuration = Math.max(0, Math.min(durationDays, 30));
        vector.add(safeDuration / 30f);
        return vector;
    }

    private Set<String> cleanSymptoms(List<String> symptoms) {
        Set<String> cleaned = new HashSet<>();
        if (symptoms == null) {
            return cleaned;
        }

        for (String symptom : symptoms) {
            if (symptom == null) {
                continue;
            }
            String normalized = symptom.trim().toLowerCase(Locale.US);
            if (!normalized.isEmpty()) {
                cleaned.add(normalized);
            }
        }
        return cleaned;
    }

    private float normalizeSeverity(String severity) {
        if (severity == null) {
            return 0.33f;
        }
        String normalized = severity.trim().toLowerCase(Locale.US);
        switch (normalized) {
            case "high":
                return 1.0f;
            case "medium":
                return 0.66f;
            case "low":
            default:
                return 0.33f;
        }
    }
}
