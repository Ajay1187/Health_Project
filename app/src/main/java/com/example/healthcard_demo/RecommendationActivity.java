package com.example.healthcard_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import java.util.List;

public class RecommendationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendation);

        String disease = getIntent().getStringExtra("disease");

        RecommendationEngine engine = new RecommendationEngine(this);
        DiseaseResponse recommendation = engine.getRecommendation(disease);

        TextView tvTitle = findViewById(R.id.tv_reco_title);
        TextView tvDiseaseName = findViewById(R.id.tv_reco_disease_name);
        TextView tvDescription = findViewById(R.id.tv_reco_description);
        TextView tvPrecautions = findViewById(R.id.tv_reco_precautions);
        TextView tvDoctor = findViewById(R.id.tv_reco_doctor);
        TextView tvDiet = findViewById(R.id.tv_reco_diet);
        TextView tvExercise = findViewById(R.id.tv_reco_exercise);

        tvTitle.setText("Recommendation");
        tvDiseaseName.setText("Disease Name: " + (TextUtils.isEmpty(disease) ? "Unknown" : disease));
        tvDescription.setText("Disease Description: " + safeText(recommendation.getDescription()));
        tvPrecautions.setText(toBullets("Disease Precaution", recommendation.getPrecautions()));
        tvDoctor.setText("Doctor Details: " + safeText(recommendation.getDoctorDetails()));
        tvDiet.setText("Diet Plan: " + safeText(recommendation.getDietPlan()));
        tvExercise.setText("Exercise: " + safeText(recommendation.getExercise()));
    }

    private String safeText(String value) {
        return TextUtils.isEmpty(value) ? "Not available in dataset." : value;
    }

    private String toBullets(String header, List<String> values) {
        if (values == null || values.isEmpty()) {
            return header + ": Not available in dataset.";
        }
        StringBuilder sb = new StringBuilder(header).append(":\n");
        for (String value : values) {
            sb.append("• ").append(value).append("\n");
        }
        return sb.toString().trim();
    }
}
