package com.example.healthcard_demo;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class DiseaseDataRepository {
    private static final String DATASET_FILE = "disease_data/dataset.csv";
    private static final String PRECAUTION_FILE = "disease_data/symptom_precaution.csv";
    private static final String SEVERITY_FILE = "disease_data/Symptom-severity.csv";
    private static final String DETAILS_FILE = "disease_data/disease_details.csv";

    private static Map<String, DiseaseProfile> cachedProfiles;
    private static Map<String, Integer> cachedSymptomSeverity;
    private static List<String> cachedAllSymptoms;

    private final Context context;

    public DiseaseDataRepository(Context context) {
        this.context = context.getApplicationContext();
        ensureLoaded();
    }

    public List<String> getAllSymptoms() {
        return cachedAllSymptoms;
    }


    public DiseaseResponse predictFromSymptoms(List<String> symptoms, int durationDays, float temperatureF) {
        ensureLoaded();

        List<String> normalizedSymptoms = normalizeSymptoms(symptoms);
        if (normalizedSymptoms.isEmpty()) {
            return buildResponseForPrediction(
                    "General Viral Infection",
                    0.35f,
                    symptoms,
                    durationDays,
                    temperatureF
            );
        }

        DiseaseProfile bestProfile = null;
        float bestScore = -1f;
        for (DiseaseProfile profile : cachedProfiles.values()) {
            if (profile.symptoms.isEmpty()) {
                continue;
            }

            int matches = 0;
            for (String symptom : normalizedSymptoms) {
                if (profile.symptoms.contains(symptom)) {
                    matches++;
                }
            }

            if (matches == 0) {
                continue;
            }

            float precision = matches / (float) normalizedSymptoms.size();
            float recall = matches / (float) profile.symptoms.size();
            float overlapScore = (precision * 0.7f) + (recall * 0.3f);
            if (overlapScore > bestScore) {
                bestScore = overlapScore;
                bestProfile = profile;
            }
        }

        if (bestProfile == null) {
            return buildResponseForPrediction(
                    "General Viral Infection",
                    0.4f,
                    symptoms,
                    durationDays,
                    temperatureF
            );
        }

        float confidence = Math.max(0.4f, Math.min(0.92f, bestScore));
        return buildResponseForPrediction(
                bestProfile.diseaseName,
                confidence,
                symptoms,
                durationDays,
                temperatureF
        );
    }

    public DiseaseResponse buildResponseForPrediction(String diseaseName, float confidence, List<String> symptoms, int durationDays, float temperatureF) {
        ensureLoaded();

        List<String> normalizedSymptoms = normalizeSymptoms(symptoms);
        String inputSeverity = deriveInputSeverity(durationDays, temperatureF);
        int inputSeverityScore = inputSeverityToScore(inputSeverity);
        DiseaseProfile profile = cachedProfiles.get(normalizeDiseaseKey(diseaseName));

        DiseaseResponse response = new DiseaseResponse();
        response.setPredictionTimestamp(System.currentTimeMillis());
        response.setInputSeverity(inputSeverity);

        if (profile == null) {
            response.setPredictedDisease("General Viral Infection");
            response.setDescription("CNN model prediction did not map to a known disease profile.");
            response.setPrecautions(defaultPrecautions());
            response.setDoctorDetails("General Physician - Community Health Clinic - +91-90000-00001");
            response.setDietPlan("Hydration, warm fluids, and balanced home-cooked meals.");
            response.setExercise("Take adequate rest and perform light walking only if comfortable.");
            response.setSeverityScore(inputSeverityScore);
            response.setSeverity(mapSeverityLabel(inputSeverityScore));
            response.setConfidence(confidence);
            return response;
        }

        int matchedCount = 0;
        int matchedSeveritySum = 0;
        for (String symptom : normalizedSymptoms) {
            if (profile.symptoms.contains(symptom)) {
                matchedCount++;
                matchedSeveritySum += getSymptomSeverity(symptom);
            }
        }

        int averageMatchedSeverity = matchedCount == 0
                ? inputSeverityScore
                : Math.round(matchedSeveritySum / (float) matchedCount);
        int severityScore = Math.max(1, Math.min(7,
                Math.round((averageMatchedSeverity * 0.6f) + (inputSeverityScore * 0.4f))));

        response.setPredictedDisease(profile.diseaseName);
        response.setDescription(profile.description);
        response.setPrecautions(profile.precautions);
        response.setDoctorDetails(profile.doctorDetails);
        response.setDietPlan(profile.dietPlan);
        response.setExercise(profile.exercise);
        response.setConfidence(Math.max(0f, Math.min(1f, confidence)));
        response.setSeverityScore(severityScore);
        response.setSeverity(mapSeverityLabel(severityScore));
        return response;
    }

    public DiseaseResponse getDetailsForDisease(String diseaseName) {
        ensureLoaded();
        DiseaseProfile profile = cachedProfiles.get(normalizeDiseaseKey(diseaseName));
        DiseaseResponse response = new DiseaseResponse();
        response.setPredictedDisease(diseaseName);
        response.setPredictionTimestamp(System.currentTimeMillis());
        if (profile == null) {
            response.setDescription("No additional dataset-backed details were found for this disease.");
            response.setPrecautions(defaultPrecautions());
            response.setDoctorDetails("General Physician - Community Health Clinic - +91-90000-00001");
            response.setDietPlan("Balanced diet with good hydration.");
            response.setExercise("Light stretching and rest.");
            return response;
        }

        response.setDescription(profile.description);
        response.setPrecautions(profile.precautions);
        response.setDoctorDetails(profile.doctorDetails);
        response.setDietPlan(profile.dietPlan);
        response.setExercise(profile.exercise);
        return response;
    }

    private void ensureLoaded() {
        if (cachedProfiles != null && cachedSymptomSeverity != null && cachedAllSymptoms != null) {
            return;
        }

        cachedProfiles = new LinkedHashMap<>();
        cachedSymptomSeverity = new HashMap<>();

        loadSymptomSeverity();
        loadDiseaseDataset();
        loadPrecautions();
        loadDiseaseDetails();

        Set<String> allSymptoms = new HashSet<>(cachedSymptomSeverity.keySet());
        for (DiseaseProfile profile : cachedProfiles.values()) {
            allSymptoms.addAll(profile.symptoms);
        }
        cachedAllSymptoms = new ArrayList<>(allSymptoms);
        Collections.sort(cachedAllSymptoms);
    }

    private void loadSymptomSeverity() {
        List<List<String>> rows = readCsv(SEVERITY_FILE);
        for (int i = 1; i < rows.size(); i++) {
            List<String> row = rows.get(i);
            if (row.size() < 2) {
                continue;
            }
            String symptom = normalizeSymptom(row.get(0));
            if (TextUtils.isEmpty(symptom)) {
                continue;
            }
            try {
                cachedSymptomSeverity.put(symptom, Integer.parseInt(row.get(1).trim()));
            } catch (NumberFormatException ignored) {
                cachedSymptomSeverity.put(symptom, 3);
            }
        }
    }

    private void loadDiseaseDataset() {
        List<List<String>> rows = readCsv(DATASET_FILE);
        for (int i = 1; i < rows.size(); i++) {
            List<String> row = rows.get(i);
            if (row.isEmpty()) {
                continue;
            }
            String diseaseName = safeCell(row, 0);
            if (TextUtils.isEmpty(diseaseName)) {
                continue;
            }
            DiseaseProfile profile = getOrCreateProfile(diseaseName);
            for (int column = 1; column < row.size(); column++) {
                String symptom = normalizeSymptom(row.get(column));
                if (!TextUtils.isEmpty(symptom)) {
                    profile.symptoms.add(symptom);
                }
            }
        }
    }

    private void loadPrecautions() {
        List<List<String>> rows = readCsv(PRECAUTION_FILE);
        for (int i = 1; i < rows.size(); i++) {
            List<String> row = rows.get(i);
            String diseaseName = safeCell(row, 0);
            if (TextUtils.isEmpty(diseaseName)) {
                continue;
            }
            DiseaseProfile profile = getOrCreateProfile(diseaseName);
            profile.precautions.clear();
            for (int column = 1; column < row.size(); column++) {
                String precaution = safeCell(row, column);
                if (!TextUtils.isEmpty(precaution)) {
                    profile.precautions.add(precaution);
                }
            }
        }
    }

    private void loadDiseaseDetails() {
        List<List<String>> rows = readCsv(DETAILS_FILE);
        for (int i = 1; i < rows.size(); i++) {
            List<String> row = rows.get(i);
            String diseaseName = safeCell(row, 0);
            if (TextUtils.isEmpty(diseaseName)) {
                continue;
            }
            DiseaseProfile profile = getOrCreateProfile(diseaseName);
            profile.description = safeCell(row, 1);
            profile.doctorDetails = safeCell(row, 2);
            profile.exercise = safeCell(row, 3);
            profile.dietPlan = safeCell(row, 4);
        }
    }

    private DiseaseProfile getOrCreateProfile(String diseaseName) {
        String key = normalizeDiseaseKey(diseaseName);
        DiseaseProfile profile = cachedProfiles.get(key);
        if (profile == null) {
            profile = new DiseaseProfile();
            profile.diseaseName = diseaseName.trim();
            profile.description = "Description will be added soon.";
            profile.doctorDetails = "General Physician - Community Health Clinic - +91-90000-00001";
            profile.dietPlan = "Balanced meals and plenty of water.";
            profile.exercise = "Light stretching and rest.";
            profile.precautions = defaultPrecautions();
            cachedProfiles.put(key, profile);
        }
        return profile;
    }

    private List<List<String>> readCsv(String assetPath) {
        List<List<String>> rows = new ArrayList<>();
        AssetManager assetManager = context.getAssets();
        try (InputStream inputStream = assetManager.open(assetPath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                rows.add(parseCsvLine(line));
            }
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load asset: " + assetPath, e);
        }
        return rows;
    }

    private List<String> parseCsvLine(String line) {
        List<String> values = new ArrayList<>();
        if (line == null) {
            return values;
        }

        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (ch == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (ch == ',' && !inQuotes) {
                values.add(current.toString().trim());
                current.setLength(0);
            } else {
                current.append(ch);
            }
        }
        values.add(current.toString().trim());
        return values;
    }

    private List<String> normalizeSymptoms(Collection<String> symptoms) {
        List<String> normalized = new ArrayList<>();
        if (symptoms == null) {
            return normalized;
        }
        for (String symptom : symptoms) {
            String value = normalizeSymptom(symptom);
            if (!TextUtils.isEmpty(value) && !normalized.contains(value)) {
                normalized.add(value);
            }
        }
        return normalized;
    }

    private int getSymptomSeverity(String symptom) {
        Integer severity = cachedSymptomSeverity.get(normalizeSymptom(symptom));
        return severity == null ? 3 : severity;
    }

    private String deriveInputSeverity(int durationDays, float temperatureF) {
        if (durationDays > 4 || temperatureF > 99f) {
            return "High";
        }
        if (durationDays == 4 || Math.round(temperatureF) == 99) {
            return "Medium";
        }
        return "Low";
    }

    private int inputSeverityToScore(String severity) {
        if ("High".equalsIgnoreCase(severity)) {
            return 6;
        }
        if ("Medium".equalsIgnoreCase(severity)) {
            return 4;
        }
        return 2;
    }

    public static String mapSeverityLabel(int severityScore) {
        if (severityScore <= 3) {
            return "Low";
        }
        if (severityScore == 4) {
            return "Medium";
        }
        return "High";
    }

    private List<String> defaultPrecautions() {
        List<String> values = new ArrayList<>();
        values.add("Consult a doctor for a confirmed diagnosis.");
        values.add("Stay hydrated and monitor symptoms.");
        values.add("Seek urgent care if breathing trouble or severe fever develops.");
        return values;
    }

    private String normalizeDiseaseKey(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.US);
    }

    private String normalizeSymptom(String value) {
        if (value == null) {
            return "";
        }
        return value.trim()
                .toLowerCase(Locale.US)
                .replace('_', ' ')
                .replaceAll("\\s+", " ");
    }

    private String safeCell(List<String> row, int index) {
        return index < row.size() ? row.get(index).trim() : "";
    }

    private static class DiseaseProfile {
        private String diseaseName;
        private final Set<String> symptoms = new HashSet<>();
        private String description;
        private List<String> precautions = new ArrayList<>();
        private String doctorDetails;
        private String dietPlan;
        private String exercise;
    }
}
