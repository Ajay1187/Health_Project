package com.example.healthcard_demo;

import android.content.Context;

import java.lang.reflect.Method;

public class RecommendationEngine {

    private final DiseaseDataRepository repository;

    public RecommendationEngine(Context context) {
        this.repository = new DiseaseDataRepository(context);
    }

    public DiseaseResponse getRecommendation(String diseaseName) {
        try {
            Method detailsMethod = DiseaseDataRepository.class.getMethod("getDetailsForDisease", String.class);
            Object value = detailsMethod.invoke(repository, diseaseName);
            if (value instanceof DiseaseResponse) {
                return (DiseaseResponse) value;
            }
        } catch (Exception ignored) {
            // Backward compatibility fallback for branches where getDetailsForDisease is absent.
        }

        return fallbackRecommendation(diseaseName);
    }

    private DiseaseResponse fallbackRecommendation(String diseaseName) {
        DiseaseResponse response = new DiseaseResponse();
        response.setPredictedDisease(diseaseName);
        response.setDescription("Detailed recommendation data is not available in this build.");
        response.setDoctorDetails("Please consult a general physician for guidance.");
        response.setDietPlan("Hydration and balanced diet.");
        response.setExercise("Rest and light movement as tolerated.");
        return response;
    }
}
