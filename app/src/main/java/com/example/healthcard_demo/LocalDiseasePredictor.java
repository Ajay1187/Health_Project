package com.example.healthcard_demo;

import android.content.Context;

import java.io.IOException;
import java.util.List;

public class LocalDiseasePredictor {

    private final DiseaseDataRepository repository;
    private final CnnDiseaseClassifier cnnDiseaseClassifier;
    private final boolean usingCnnModel;

    public LocalDiseasePredictor(Context context) {
        this.repository = new DiseaseDataRepository(context);

        CnnDiseaseClassifier cnnClassifier = null;
        boolean cnnAvailable = false;
        try {
            cnnClassifier = new CnnDiseaseClassifier(context);
            cnnAvailable = true;
        } catch (IOException ignored) {
            // Fall back to dataset overlap prediction when CNN model is not bundled.
        }

        this.cnnDiseaseClassifier = cnnClassifier;
        this.usingCnnModel = cnnAvailable;
    }

    public DiseaseResponse predict(List<String> symptoms, int durationDays, float temperatureF) {
        if (usingCnnModel && cnnDiseaseClassifier != null) {
            CnnDiseaseClassifier.Prediction prediction = cnnDiseaseClassifier.predict(symptoms);
            return repository.buildResponseForPrediction(
                    prediction.getDiseaseName(),
                    prediction.getConfidence(),
                    symptoms,
                    durationDays,
                    temperatureF
            );
        }

        return repository.predictFromSymptoms(symptoms, durationDays, temperatureF);
    }
}
