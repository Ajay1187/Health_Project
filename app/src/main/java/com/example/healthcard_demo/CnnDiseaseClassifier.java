package com.example.healthcard_demo;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;

import org.tensorflow.lite.Interpreter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class CnnDiseaseClassifier {
    private static final String MODEL_FILE = "disease_data/disease_cnn.tflite";
    private static final String FEATURE_FILE = "disease_data/Symptom-severity.csv";
    private static final String DATASET_FILE = "disease_data/dataset.csv";

    public static class Prediction {
        private final String diseaseName;
        private final float confidence;

        public Prediction(String diseaseName, float confidence) {
            this.diseaseName = diseaseName;
            this.confidence = confidence;
        }

        public String getDiseaseName() {
            return diseaseName;
        }

        public float getConfidence() {
            return confidence;
        }
    }

    private final Interpreter interpreter;
    private final List<String> featureNames;
    private final List<String> labels;

    public CnnDiseaseClassifier(Context context) throws IOException {
        AssetManager assetManager = context.getAssets();
        this.interpreter = new Interpreter(loadModelFile(assetManager));
        this.featureNames = loadFeatureNames(assetManager);
        this.labels = loadLabels(assetManager);
    }

    public Prediction predict(Collection<String> symptoms) {
        Set<String> normalizedSymptoms = normalizeSymptoms(symptoms);
        float[][][] input = new float[1][featureNames.size()][1];
        for (int index = 0; index < featureNames.size(); index++) {
            input[0][index][0] = normalizedSymptoms.contains(featureNames.get(index)) ? 1f : 0f;
        }

        float[][] output = new float[1][labels.size()];
        interpreter.run(input, output);

        int bestIndex = 0;
        float bestScore = output[0][0];
        for (int index = 1; index < output[0].length; index++) {
            if (output[0][index] > bestScore) {
                bestScore = output[0][index];
                bestIndex = index;
            }
        }

        String predictedDisease = labels.isEmpty() ? "General Viral Infection" : labels.get(bestIndex);
        return new Prediction(predictedDisease, Math.max(0f, Math.min(1f, bestScore)));
    }

    private MappedByteBuffer loadModelFile(AssetManager assetManager) throws IOException {
        AssetFileDescriptor fileDescriptor = assetManager.openFd(MODEL_FILE);
        try (FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
             FileChannel fileChannel = inputStream.getChannel()) {
            long startOffset = fileDescriptor.getStartOffset();
            long declaredLength = fileDescriptor.getDeclaredLength();
            return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
        }
    }

    private List<String> loadFeatureNames(AssetManager assetManager) throws IOException {
        List<String> names = new ArrayList<>();
        try (InputStream inputStream = assetManager.open(FEATURE_FILE);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] columns = line.split(",", 2);
                if (columns.length > 0) {
                    String normalized = normalize(columns[0]);
                    if (!normalized.isEmpty()) {
                        names.add(normalized);
                    }
                }
            }
        }
        return names;
    }

    private List<String> loadLabels(AssetManager assetManager) throws IOException {
        Set<String> diseases = new LinkedHashSet<>();
        try (InputStream inputStream = assetManager.open(DATASET_FILE);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] columns = line.split(",", 2);
                if (columns.length > 0) {
                    String disease = columns[0].trim();
                    if (!disease.isEmpty()) {
                        diseases.add(disease);
                    }
                }
            }
        }
        List<String> labels = new ArrayList<>(diseases);
        Collections.sort(labels);
        return labels;
    }

    private Set<String> normalizeSymptoms(Collection<String> symptoms) {
        Set<String> normalized = new HashSet<>();
        if (symptoms == null) {
            return normalized;
        }
        for (String symptom : symptoms) {
            String value = normalize(symptom);
            if (!value.isEmpty()) {
                normalized.add(value);
            }
        }
        return normalized;
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }
        return value.trim().toLowerCase(Locale.US).replace('_', ' ').replaceAll("\\s+", " ");
    }
}
