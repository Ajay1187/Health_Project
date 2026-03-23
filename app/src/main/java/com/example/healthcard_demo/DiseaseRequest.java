package com.example.healthcard_demo;

import java.util.List;

public class DiseaseRequest {

    private List<Float> features;

    public DiseaseRequest(List<Float> features) {
        this.features = features;
    }

    public List<Float> getFeatures() {
        return features;
    }

    public void setFeatures(List<Float> features) {
        this.features = features;
    }
}
