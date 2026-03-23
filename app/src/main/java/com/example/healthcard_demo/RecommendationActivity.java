package com.example.healthcard_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import java.util.List;

public class RecommendationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendation);

        String disease = getIntent().getStringExtra("disease");

        RecommendationEngine engine = new RecommendationEngine();
        RecommendationEngine.Recommendation recommendation = engine.getRecommendation(disease);

        TextView tvTitle = findViewById(R.id.tv_reco_title);
        TextView tvMedicine = findViewById(R.id.tv_reco_medicines);
        TextView tvDiet = findViewById(R.id.tv_reco_diet);
        TextView tvExercise = findViewById(R.id.tv_reco_exercise);
        TextView tvPrecautions = findViewById(R.id.tv_reco_precautions);
        TextView tvDoctors = findViewById(R.id.tv_reco_doctors);

        tvTitle.setText("Recommendations for: " + (disease == null ? "Unknown" : disease));
        tvMedicine.setText(toBullets("Medicines", recommendation.getMedicines()));
        tvDiet.setText(toBullets("Diet Plan", recommendation.getDietPlan()));
        tvExercise.setText(toBullets("Exercise", recommendation.getExercises()));
        tvPrecautions.setText(toBullets("Precautions", recommendation.getPrecautions()));
        tvDoctors.setText(toBullets("Doctors", recommendation.getDoctors()));
    }

    private String toBullets(String header, List<String> values) {
        StringBuilder sb = new StringBuilder(header).append(":\n");
        for (String value : values) {
            sb.append("• ").append(value).append("\n");
        }
        return sb.toString();
    }
}
