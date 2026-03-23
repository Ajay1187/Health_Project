package com.example.healthcard_demo;

import android.content.Context;

public class RecommendationEngine {

    private final DiseaseDataRepository repository;

    public RecommendationEngine(Context context) {
        this.repository = new DiseaseDataRepository(context);
    }

    public DiseaseResponse getRecommendation(String diseaseName) {
        return repository.getDetailsForDisease(diseaseName);
    }
}
