package com.example.healthcard_demo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RecommendationEngine {

    public static class Recommendation {
        private final List<String> medicines;
        private final List<String> dietPlan;
        private final List<String> exercises;
        private final List<String> precautions;
        private final List<String> doctors;

        public Recommendation(List<String> medicines, List<String> dietPlan, List<String> exercises,
                              List<String> precautions, List<String> doctors) {
            this.medicines = medicines;
            this.dietPlan = dietPlan;
            this.exercises = exercises;
            this.precautions = precautions;
            this.doctors = doctors;
        }

        public List<String> getMedicines() { return medicines; }
        public List<String> getDietPlan() { return dietPlan; }
        public List<String> getExercises() { return exercises; }
        public List<String> getPrecautions() { return precautions; }
        public List<String> getDoctors() { return doctors; }
    }

    private static final Recommendation DEFAULT_RECOMMENDATION = new Recommendation(
            Arrays.asList("Consult a physician before any medicine"),
            Arrays.asList("Hydration", "Balanced diet with fruits and vegetables"),
            Arrays.asList("Light stretching", "Short walks"),
            Arrays.asList("Monitor symptoms daily", "Avoid self-medication"),
            Arrays.asList("General Physician - City Hospital (+91-90000-00001)")
    );

    private final Map<String, Recommendation> recommendationMap = new HashMap<>();

    public RecommendationEngine() {
        recommendationMap.put("flu", new Recommendation(
                Arrays.asList("Paracetamol", "Cetirizine"),
                Arrays.asList("Warm fluids", "Vitamin-C rich foods"),
                Arrays.asList("Breathing exercise 10 mins", "Rest"),
                Arrays.asList("Isolate if fever", "Wear mask"),
                Arrays.asList("Dr. Mehta (Pulmonologist) - Apollo Clinic (+91-90000-00002)")
        ));

        recommendationMap.put("dengue", new Recommendation(
                Arrays.asList("Paracetamol (avoid NSAIDs)"),
                Arrays.asList("ORS", "Papaya leaf extract (doctor advice)", "High fluid intake"),
                Arrays.asList("Bed rest"),
                Arrays.asList("Check platelets regularly", "Immediate ER visit if bleeding"),
                Arrays.asList("Dr. Rao (Internal Medicine) - Metro Hospital (+91-90000-00003)")
        ));

        recommendationMap.put("bronchitis", new Recommendation(
                Arrays.asList("Expectorant syrup", "Doctor-prescribed bronchodilator"),
                Arrays.asList("Warm soup", "Avoid cold beverages"),
                Arrays.asList("Steam inhalation", "Pursed-lip breathing"),
                Arrays.asList("Avoid smoke", "Use humidifier"),
                Arrays.asList("Dr. Shah (Chest Specialist) - City Lung Center (+91-90000-00004)")
        ));

        recommendationMap.put("migraine", new Recommendation(
                Arrays.asList("Doctor-prescribed triptan", "Paracetamol"),
                Arrays.asList("Regular meals", "Reduce caffeine"),
                Arrays.asList("Neck stretches", "Yoga"),
                Arrays.asList("Avoid triggers", "Sleep schedule"),
                Arrays.asList("Dr. Iyer (Neurologist) - Neuro Care (+91-90000-00005)")
        ));
    }

    public Recommendation getRecommendation(String diseaseName) {
        if (diseaseName == null) {
            return DEFAULT_RECOMMENDATION;
        }
        Recommendation recommendation = recommendationMap.get(diseaseName.trim().toLowerCase(Locale.US));
        return recommendation == null ? DEFAULT_RECOMMENDATION : recommendation;
    }
}
