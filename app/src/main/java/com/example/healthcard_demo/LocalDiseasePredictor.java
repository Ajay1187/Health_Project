package com.example.healthcard_demo;

import android.content.Context;
import android.content.res.AssetManager;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class LocalDiseasePredictor {

    private static final String NB_MODEL_FILE = "disease_data/disease_nb_model.json";

    private final DiseaseDataRepository repository;
    private final CnnDiseaseClassifier cnnDiseaseClassifier;
    private final TrainedDiseaseClassifier trainedDiseaseClassifier;
    private final boolean usingCnnModel;
    private final boolean usingNaiveBayesModel;

    public LocalDiseasePredictor(Context context) {
        this.repository = new DiseaseDataRepository(context);

        CnnDiseaseClassifier cnnClassifier = null;
        boolean cnnAvailable = false;
        try {
            cnnClassifier = new CnnDiseaseClassifier(context);
            cnnAvailable = true;
        } catch (IOException ignored) {
            // Fall back to packaged Naive Bayes model, then dataset overlap prediction.
        }

        TrainedDiseaseClassifier nbClassifier = null;
        boolean nbAvailable = false;
        if (!cnnAvailable) {
            nbClassifier = loadNaiveBayesClassifier(context.getAssets());
            nbAvailable = nbClassifier != null && nbClassifier.isReady();
        }

        this.cnnDiseaseClassifier = cnnClassifier;
        this.usingCnnModel = cnnAvailable;
        this.trainedDiseaseClassifier = nbClassifier;
        this.usingNaiveBayesModel = nbAvailable;
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

        if (usingNaiveBayesModel && trainedDiseaseClassifier != null) {
            TrainedDiseaseClassifier.Prediction prediction = trainedDiseaseClassifier.predict(symptoms);
            if (prediction != null) {
                return repository.buildResponseForPrediction(
                        prediction.getDiseaseName(),
                        prediction.getConfidence(),
                        symptoms,
                        durationDays,
                        temperatureF
                );
            }
        }

        return repository.predictFromSymptoms(symptoms, durationDays, temperatureF);
    }

    private TrainedDiseaseClassifier loadNaiveBayesClassifier(AssetManager assetManager) {
        try (InputStream inputStream = assetManager.open(NB_MODEL_FILE);
             InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            TrainedDiseaseClassifier.ModelData modelData =
                    new Gson().fromJson(reader, TrainedDiseaseClassifier.ModelData.class);
            TrainedDiseaseClassifier classifier = new TrainedDiseaseClassifier(modelData);
            return classifier.isReady() ? classifier : null;
        } catch (Exception ignored) {
            return null;
        }
    }
}
