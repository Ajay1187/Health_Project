package com.example.healthcard_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class EnterSymptomsActivity extends AppCompatActivity {

    private static final Locale COMPAT_LOCALE = Locale.US;

    private static final List<String> PREDEFINED_SYMPTOMS = Arrays.asList(
            "itching",
            "skin_rash",
            "nodal_skin_eruptions",
            "continuous_sneezing",
            "shivering",
            "chills",
            "joint_pain",
            "stomach_pain",
            "acidity",
            "ulcers_on_tongue",
            "muscle_wasting",
            "vomiting",
            "burning_micturition",
            "spotting_urination",
            "fatigue",
            "weight_gain",
            "anxiety",
            "cold_hands_and_feets",
            "mood_swings",
            "weight_loss",
            "restlessness",
            "lethargy",
            "patches_in_throat",
            "irregular_sugar_level",
            "cough",
            "high_fever",
            "sunken_eyes",
            "breathlessness",
            "sweating",
            "dehydration",
            "indigestion",
            "headache",
            "yellowish_skin",
            "dark_urine",
            "nausea",
            "loss_of_appetite",
            "pain_behind_the_eyes",
            "back_pain",
            "constipation",
            "abdominal_pain",
            "diarrhoea",
            "mild_fever",
            "yellow_urine",
            "yellowing_of_eyes",
            "acute_liver_failure",
            "fluid_overload",
            "swelling_of_stomach",
            "swelled_lymph_nodes",
            "malaise",
            "blurred_and_distorted_vision",
            "phlegm",
            "throat_irritation",
            "redness_of_eyes",
            "sinus_pressure",
            "runny_nose",
            "congestion",
            "chest_pain",
            "weakness_in_limbs",
            "fast_heart_rate",
            "pain_during_bowel_movements",
            "pain_in_anal_region",
            "bloody_stool",
            "irritation_in_anus",
            "neck_pain",
            "dizziness",
            "cramps",
            "bruising",
            "obesity",
            "swollen_legs",
            "swollen_blood_vessels",
            "puffy_face_and_eyes",
            "enlarged_thyroid",
            "brittle_nails",
            "swollen_extremeties",
            "excessive_hunger",
            "extra_marital_contacts",
            "drying_and_tingling_lips",
            "slurred_speech",
            "knee_pain",
            "hip_joint_pain",
            "muscle_weakness",
            "stiff_neck",
            "swelling_joints",
            "movement_stiffness",
            "spinning_movements",
            "loss_of_balance",
            "unsteadiness",
            "weakness_of_one_body_side",
            "loss_of_smell",
            "bladder_discomfort",
            "foul_smell_ofurine",
            "continuous_feel_of_urine",
            "passage_of_gases",
            "internal_itching",
            "toxic_look_(typhos)",
            "depression",
            "irritability",
            "muscle_pain",
            "altered_sensorium",
            "red_spots_over_body",
            "belly_pain",
            "abnormal_menstruation",
            "dischromic_patches",
            "watering_from_eyes",
            "increased_appetite",
            "polyuria",
            "family_history",
            "mucoid_sputum",
            "rusty_sputum",
            "lack_of_concentration",
            "visual_disturbances",
            "receiving_blood_transfusion",
            "receiving_unsterile_injections",
            "coma",
            "stomach_bleeding",
            "distention_of_abdomen",
            "history_of_alcohol_consumption",
            "blood_in_sputum",
            "prominent_veins_on_calf",
            "palpitations",
            "painful_walking",
            "pus_filled_pimples",
            "blackheads",
            "scurring",
            "skin_peeling",
            "silver_like_dusting",
            "small_dents_in_nails",
            "inflammatory_nails",
            "blister",
            "red_sore_around_nose",
            "yellow_crust_ooze"
    );

    private MultiAutoCompleteTextView etSymptoms;
    private EditText etDuration;
    private EditText etTemperature;
    private TextView tvCalculatedSeverity;
    private Button btnPredict;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_symptoms);

        etSymptoms = findViewById(R.id.et_symptoms);
        etDuration = findViewById(R.id.et_duration);
        etTemperature = findViewById(R.id.et_temperature);
        tvCalculatedSeverity = findViewById(R.id.tv_calculated_severity);
        btnPredict = findViewById(R.id.btn_predict);

        ArrayAdapter<String> symptomsAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line,
                PREDEFINED_SYMPTOMS);
        etSymptoms.setAdapter(symptomsAdapter);
        etSymptoms.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());


        btnPredict.setOnClickListener(v -> submitPrediction());
    }

    private void updateCalculatedSeverity() {
        int duration = parseInt(etDuration.getText().toString().trim());
        float temperature = parseFloat(etTemperature.getText().toString().trim());
        String severity;
        if (duration > 4 || temperature > 99f) {
            severity = "High";
        } else if (duration == 4 || Math.round(temperature) == 99) {
            severity = "Medium";
        } else {
            severity = "Low";
        }
        tvCalculatedSeverity.setText(String.format(Locale.US,
                "Calculated Severity: %s (High if duration > 4 or temperature > 99°F; Medium if equal to 4 days or 99°F; else Low)", severity));
    }

    private void submitPrediction() {
        String rawSymptoms = etSymptoms.getText().toString().trim();
        rawSymptoms = rawSymptoms.replaceAll(",\\s*$", "").trim();
        etSymptoms.setText(rawSymptoms);
        etSymptoms.setSelection(rawSymptoms.length());

        String durationText = etDuration.getText().toString().trim();
        String temperatureText = etTemperature.getText().toString().trim();

        if (TextUtils.isEmpty(rawSymptoms) || TextUtils.isEmpty(durationText) || TextUtils.isEmpty(temperatureText)) {
            Toast.makeText(this, "Please complete symptoms, duration, and temperature.", Toast.LENGTH_SHORT).show();
            return;
        }

        int durationDays;
        float temperatureF;
        try {
            durationDays = Integer.parseInt(durationText);
            if (durationDays <= 0) {
                Toast.makeText(this, "Duration must be greater than 0 days.", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Duration must be numeric.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            temperatureF = Float.parseFloat(temperatureText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Temperature must be numeric.", Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> symptomList = new ArrayList<>();
        for (String symptom : rawSymptoms.split(",")) {
            String clean = symptom.trim();
            if (!clean.isEmpty()) {
                symptomList.add(clean);
            }
        }

        if (symptomList.isEmpty()) {
            Toast.makeText(this, "Please enter at least one symptom.", Toast.LENGTH_SHORT).show();
            return;
        }

        btnPredict.setEnabled(false);
        DiseaseResponse result = new LocalDiseasePredictor(this).predict(symptomList, durationDays, temperatureF);
        btnPredict.setEnabled(true);

        Intent intent = new Intent(EnterSymptomsActivity.this, PredictionResultActivity.class);
        intent.putExtra("disease", safeText(result.getPredictedDisease(), "General Viral Infection"));
        intent.putExtra("severity", safeText(result.getSeverity(), "Low"));
        intent.putExtra("severityScore", result.getSeverityScore() <= 0 ? 1 : result.getSeverityScore());
        intent.putExtra("predictionTimestamp", result.getPredictionTimestamp());
        intent.putExtra("description", safeText(result.getDescription(), "Description unavailable."));
        startActivity(intent);
    }

    private int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private float parseFloat(String value) {
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            return 0f;
        }
    }

    private String safeText(String value, String fallback) {
        return TextUtils.isEmpty(value) ? fallback : value;
    }
}
