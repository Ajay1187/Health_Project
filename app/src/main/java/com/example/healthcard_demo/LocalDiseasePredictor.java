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

        CnnDiseaseClassifier classifier = null;
        boolean cnnAvailable = false;
        try {
            classifier = new CnnDiseaseClassifier(context);
            cnnAvailable = true;
        } catch (IOException ignored) {
            // Fall back to dataset-overlap prediction until the TFLite asset is bundled.
        }
        this.cnnDiseaseClassifier = classifier;
        this.usingCnnModel = cnnAvailable;
    }

    public DiseaseResponse predict(List<String> symptoms, int durationDays, float temperatureF) {
        if (!usingCnnModel || cnnDiseaseClassifier == null) {
            return repository.predictFromSymptoms(symptoms, durationDays, temperatureF);
        }

        CnnDiseaseClassifier.Prediction prediction = cnnDiseaseClassifier.predict(symptoms);
        return repository.buildResponseForPrediction(
                prediction.getDiseaseName(),
                prediction.getConfidence(),
                symptoms,
                durationDays,
                temperatureF
        );
    }
}
