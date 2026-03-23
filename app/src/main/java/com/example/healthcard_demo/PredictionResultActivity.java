package com.example.healthcard_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

public class PredictionResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prediction_result);

        TextView tvDisease = findViewById(R.id.tv_disease);
        TextView tvConfidence = findViewById(R.id.tv_confidence);
        TextView tvSeverity = findViewById(R.id.tv_severity);
        TextView tvLatency = findViewById(R.id.tv_latency);
        Button btnRecommendation = findViewById(R.id.btn_view_recommendations);
        Button btnHistory = findViewById(R.id.btn_view_history);

        String disease = getIntent().getStringExtra("disease");
        float confidence = getIntent().getFloatExtra("confidence", 0f);
        String severity = getIntent().getStringExtra("severity");
        long elapsedMs = getIntent().getLongExtra("elapsedMs", -1);

        tvDisease.setText(String.format(Locale.US, "Predicted Disease: %s", disease));
        tvConfidence.setText(String.format(Locale.US, "Confidence: %.2f%%", confidence * 100f));
        tvSeverity.setText(String.format(Locale.US, "Severity: %s", severity));
        tvLatency.setText(String.format(Locale.US, "Prediction Time: %d ms", elapsedMs));

        PredictionHistoryStore historyStore = new PredictionHistoryStore(this);
        historyStore.addItem(new PredictionHistoryItem(disease, severity, confidence, System.currentTimeMillis()));

        btnRecommendation.setOnClickListener(v -> {
            Intent recommendationIntent = new Intent(PredictionResultActivity.this, RecommendationActivity.class);
            recommendationIntent.putExtra("disease", disease);
            startActivity(recommendationIntent);
        });

        btnHistory.setOnClickListener(v -> {
            Intent historyIntent = new Intent(PredictionResultActivity.this, PredictionHistoryActivity.class);
            startActivity(historyIntent);
        });
    }
}
