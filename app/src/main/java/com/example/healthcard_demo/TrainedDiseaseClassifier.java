package com.example.healthcard_demo;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TrainedDiseaseClassifier {

    public static class ModelData {
        List<String> symptoms;
        List<DiseaseWeights> diseases;
    }

    public static class DiseaseWeights {
        String name;
        double logPrior;
        double[] logPresent;
        double[] logAbsent;
    }

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

    private final ModelData modelData;
    private final Map<String, Integer> symptomIndexMap = new HashMap<>();

    public TrainedDiseaseClassifier(ModelData modelData) {
        this.modelData = modelData;
        if (modelData != null && modelData.symptoms != null) {
            for (int index = 0; index < modelData.symptoms.size(); index++) {
                symptomIndexMap.put(normalizeSymptom(modelData.symptoms.get(index)), index);
            }
        }
    }

    public boolean isReady() {
        return modelData != null
                && modelData.symptoms != null
                && modelData.diseases != null
                && !modelData.symptoms.isEmpty()
                && !modelData.diseases.isEmpty();
    }

    public Prediction predict(Collection<String> symptoms) {
        if (!isReady()) {
            return null;
        }

        boolean[] presentFlags = new boolean[modelData.symptoms.size()];
        if (symptoms != null) {
            for (String symptom : symptoms) {
                Integer index = symptomIndexMap.get(normalizeSymptom(symptom));
                if (index != null) {
                    presentFlags[index] = true;
                }
            }
        }

        String bestDisease = null;
        double bestScore = Double.NEGATIVE_INFINITY;
        double[] scores = new double[modelData.diseases.size()];

        for (int diseaseIndex = 0; diseaseIndex < modelData.diseases.size(); diseaseIndex++) {
            DiseaseWeights diseaseWeights = modelData.diseases.get(diseaseIndex);
            double score = diseaseWeights.logPrior;
            for (int symptomIndex = 0; symptomIndex < presentFlags.length; symptomIndex++) {
                score += presentFlags[symptomIndex]
                        ? diseaseWeights.logPresent[symptomIndex]
                        : diseaseWeights.logAbsent[symptomIndex];
            }
            scores[diseaseIndex] = score;
            if (score > bestScore) {
                bestScore = score;
                bestDisease = diseaseWeights.name;
            }
        }

        double maxScore = bestScore;
        double totalExp = 0d;
        double winningExp = 0d;
        for (int diseaseIndex = 0; diseaseIndex < scores.length; diseaseIndex++) {
            double value = Math.exp(scores[diseaseIndex] - maxScore);
            totalExp += value;
            if (modelData.diseases.get(diseaseIndex).name.equals(bestDisease)) {
                winningExp = value;
            }
        }

        float confidence = totalExp == 0d ? 0f : (float) (winningExp / totalExp);
        return new Prediction(bestDisease, confidence);
    }

    private String normalizeSymptom(String value) {
        if (value == null) {
            return "";
        }
        return value.trim()
                .replace('_', ' ')
                .replaceAll("\\s+", " ")
                .toLowerCase(Locale.US);
    }
}
