```java
package com.example.healthcard_demo;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class DiseaseDataRepository {

    private static final String DATASET_FILE = "disease_data/dataset.csv";
    private static final String PRECAUTION_FILE = "disease_data/symptom_precaution.csv";
    private static final String SEVERITY_FILE = "disease_data/Symptom-severity.csv";
    private static final String DETAILS_FILE = "disease_data/disease_details.csv";
    private static final String TRAINED_MODEL_FILE = "disease_data/disease_nb_model.json";

    private static Map<String, DiseaseProfile> cachedProfiles;
    private static Map<String, Integer> cachedSymptomSeverity;
    private static List<String> cachedAllSymptoms;
    private static TrainedDiseaseClassifier cachedClassifier;

    private final Context context;

    public DiseaseDataRepository(Context context) {
        this.context = context.getApplicationContext();
        ensureLoaded();
    }

    public List<String> getAllSymptoms() {
        return cachedAllSymptoms;
    }

    public DiseaseResponse predictDisease(List<String> symptoms, int durationDays, float temperatureF) {
        ensureLoaded();

        List<String> normalizedSymptoms = normalizeSymptoms(symptoms);
        String inputSeverity = deriveInputSeverity(durationDays, temperatureF);
        int inputSeverityScore = inputSeverityToScore(inputSeverity);

        MatchResult heuristicMatch = findBestHeuristicMatch(normalizedSymptoms);
        MatchResult selectedMatch = heuristicMatch;
        float modelConfidence = heuristicMatch == null ? 0.35f : heuristicMatch.score;

        // ML Prediction
        if (cachedClassifier != null && cachedClassifier.isReady() && !normalizedSymptoms.isEmpty()) {
            TrainedDiseaseClassifier.Prediction modelPrediction =
                    cachedClassifier.predict(normalizedSymptoms);

            if (modelPrediction != null) {
                DiseaseProfile predictedProfile =
                        cachedProfiles.get(normalizeDiseaseKey(modelPrediction.getDiseaseName()));

                MatchResult modelMatch =
                        buildMatchResult(predictedProfile, normalizedSymptoms, modelPrediction.getConfidence());

                if (modelMatch != null && modelMatch.matchedCount > 0) {
                    selectedMatch = modelMatch;
                    modelConfidence = modelPrediction.getConfidence();
                }
            }
        }

        DiseaseResponse response = new DiseaseResponse();
        long now = System.currentTimeMillis();
        response.setPredictionTimestamp(now);
        response.setInputSeverity(inputSeverity);

        if (selectedMatch == null || selectedMatch.profile == null) {
            response.setPredictedDisease("General Viral Infection");
            response.setDescription("Symptoms do not strongly match a disease.");
            response.setPrecautions(defaultPrecautions());
            response.setDoctorDetails("General Physician");
            response.setDietPlan("Hydration and light diet");
            response.setExercise("Rest");
            response.setSeverityScore(inputSeverityScore);
            response.setSeverity(mapSeverityLabel(inputSeverityScore));
            response.setConfidence(0.35f);
            return response;
        }

        int avgSeverity = selectedMatch.matchedCount == 0
                ? inputSeverityScore
                : Math.round(selectedMatch.matchedSeveritySum / (float) selectedMatch.matchedCount);

        int severityScore = Math.max(1, Math.min(7,
                Math.round((avgSeverity * 0.6f) + (inputSeverityScore * 0.4f))));

        response.setPredictedDisease(selectedMatch.profile.diseaseName);
        response.setDescription(selectedMatch.profile.description);
        response.setPrecautions(selectedMatch.profile.precautions);
        response.setDoctorDetails(selectedMatch.profile.doctorDetails);
        response.setDietPlan(selectedMatch.profile.dietPlan);
        response.setExercise(selectedMatch.profile.exercise);
        response.setConfidence(Math.min(0.99f, Math.max(0.35f, modelConfidence)));
        response.setSeverityScore(severityScore);
        response.setSeverity(mapSeverityLabel(severityScore));

        return response;
    }

    private void ensureLoaded() {
        if (cachedProfiles != null) return;

        cachedProfiles = new HashMap<>();
        cachedSymptomSeverity = new HashMap<>();

        loadSymptomSeverity();
        loadDiseaseDataset();
        loadPrecautions();
        loadDiseaseDetails();
        loadTrainedModel();

        Set<String> allSymptoms = new HashSet<>(cachedSymptomSeverity.keySet());
        for (DiseaseProfile p : cachedProfiles.values()) {
            allSymptoms.addAll(p.symptoms);
        }

        cachedAllSymptoms = new ArrayList<>(allSymptoms);
        Collections.sort(cachedAllSymptoms);
    }

    private void loadTrainedModel() {
        try {
            InputStream is = context.getAssets().open(TRAINED_MODEL_FILE);
            InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
            TrainedDiseaseClassifier.ModelData data =
                    new Gson().fromJson(reader, TrainedDiseaseClassifier.ModelData.class);
            cachedClassifier = new TrainedDiseaseClassifier(data);
        } catch (Exception e) {
            cachedClassifier = null;
        }
    }

    private MatchResult findBestHeuristicMatch(List<String> symptoms) {
        MatchResult best = null;

        for (DiseaseProfile profile : cachedProfiles.values()) {
            MatchResult m = buildMatchResult(profile, symptoms, 0);

            if (m == null || m.matchedCount == 0) continue;

            float score = (float) m.matchedCount / symptoms.size();

            if (best == null || score > best.score) {
                m.score = score;
                best = m;
            }
        }
        return best;
    }

    private MatchResult buildMatchResult(DiseaseProfile profile, List<String> symptoms, float score) {
        if (profile == null) return null;

        int count = 0;
        int severity = 0;

        for (String s : symptoms) {
            if (profile.symptoms.contains(s)) {
                count++;
                severity += getSymptomSeverity(s);
            }
        }

        MatchResult r = new MatchResult();
        r.profile = profile;
        r.matchedCount = count;
        r.matchedSeveritySum = severity;
        r.score = score;
        return r;
    }

    private void loadSymptomSeverity() {
        List<List<String>> rows = readCsv(SEVERITY_FILE);
        for (int i = 1; i < rows.size(); i++) {
            List<String> row = rows.get(i);
            if (row.size() < 2) continue;

            String symptom = normalizeSymptom(row.get(0));
            if (TextUtils.isEmpty(symptom)) continue;

            try {
                cachedSymptomSeverity.put(symptom, Integer.parseInt(row.get(1).trim()));
            } catch (Exception e) {
                cachedSymptomSeverity.put(symptom, 3);
            }
        }
    }

    private void loadDiseaseDataset() {
        List<List<String>> rows = readCsv(DATASET_FILE);
        for (int i = 1; i < rows.size(); i++) {
            List<String> row = rows.get(i);
            if (row.isEmpty()) continue;

            String disease = row.get(0);
            if (TextUtils.isEmpty(disease)) continue;

            DiseaseProfile profile = getOrCreateProfile(disease);

            for (int j = 1; j < row.size(); j++) {
                String s = normalizeSymptom(row.get(j));
                if (!TextUtils.isEmpty(s)) profile.symptoms.add(s);
            }
        }
    }

    private void loadPrecautions() {
        List<List<String>> rows = readCsv(PRECAUTION_FILE);
        for (int i = 1; i < rows.size(); i++) {
            List<String> row = rows.get(i);
            String disease = row.get(0);
            if (TextUtils.isEmpty(disease)) continue;

            DiseaseProfile profile = getOrCreateProfile(disease);
            profile.precautions.clear();

            for (int j = 1; j < row.size(); j++) {
                if (!TextUtils.isEmpty(row.get(j))) {
                    profile.precautions.add(row.get(j));
                }
            }
        }
    }

    private void loadDiseaseDetails() {
        List<List<String>> rows = readCsv(DETAILS_FILE);
        for (int i = 1; i < rows.size(); i++) {
            List<String> row = rows.get(i);
            String disease = row.get(0);
            if (TextUtils.isEmpty(disease)) continue;

            DiseaseProfile profile = getOrCreateProfile(disease);
            profile.description = safe(row, 1);
            profile.doctorDetails = safe(row, 2);
            profile.exercise = safe(row, 3);
            profile.dietPlan = safe(row, 4);
        }
    }

    private DiseaseProfile getOrCreateProfile(String name) {
        String key = normalizeDiseaseKey(name);
        DiseaseProfile p = cachedProfiles.get(key);

        if (p == null) {
            p = new DiseaseProfile();
            p.diseaseName = name;
            p.precautions = defaultPrecautions();
            cachedProfiles.put(key, p);
        }
        return p;
    }

    private List<List<String>> readCsv(String file) {
        List<List<String>> rows = new ArrayList<>();

        try (InputStream is = context.getAssets().open(file);
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {

            String line;
            while ((line = br.readLine()) != null) {
                rows.add(Arrays.asList(line.split(",")));
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return rows;
    }

    private List<String> normalizeSymptoms(Collection<String> symptoms) {
        List<String> list = new ArrayList<>();
        if (symptoms == null) return list;

        for (String s : symptoms) {
            String v = normalizeSymptom(s);
            if (!TextUtils.isEmpty(v) && !list.contains(v)) {
                list.add(v);
            }
        }
        return list;
    }

    private String normalizeSymptom(String s) {
        if (s == null) return "";
        return s.trim().toLowerCase().replace("_", " ");
    }

    private int getSymptomSeverity(String s) {
        Integer val = cachedSymptomSeverity.get(s);
        return val == null ? 3 : val;
    }

    private String deriveInputSeverity(int d, float t) {
        if (d > 4 || t > 99) return "High";
        if (d == 4) return "Medium";
        return "Low";
    }

    private int inputSeverityToScore(String s) {
        if ("High".equalsIgnoreCase(s)) return 6;
        if ("Medium".equalsIgnoreCase(s)) return 4;
        return 2;
    }

    public static String mapSeverityLabel(int s) {
        if (s <= 3) return "Low";
        if (s == 4) return "Medium";
        return "High";
    }

    private List<String> defaultPrecautions() {
        return Arrays.asList(
                "Consult a doctor",
                "Stay hydrated",
                "Get proper rest"
        );
    }

    private String normalizeDiseaseKey(String v) {
        return v == null ? "" : v.trim().toLowerCase();
    }

    private String safe(List<String> row, int i) {
        return i < row.size() ? row.get(i) : "";
    }

    private static class DiseaseProfile {
        String diseaseName;
        Set<String> symptoms = new HashSet<>();
        String description;
        List<String> precautions = new ArrayList<>();
        String doctorDetails;
        String dietPlan;
        String exercise;
    }

    private static class MatchResult {
        DiseaseProfile profile;
        int matchedCount;
        int matchedSeveritySum;
        float score;
    }
}
```
