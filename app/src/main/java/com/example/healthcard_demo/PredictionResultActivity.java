package com.example.healthcard_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PredictionResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prediction_result);

        TextView tvDisease = findViewById(R.id.tv_disease);
        TextView tvSeverity = findViewById(R.id.tv_severity);
        TextView tvSeverityScore = findViewById(R.id.tv_severity_score);
        TextView tvPredictionTime = findViewById(R.id.tv_prediction_time);
        TextView tvDescription = findViewById(R.id.tv_description);
        Button btnRecommendation = findViewById(R.id.btn_view_recommendations);
        Button btnHistory = findViewById(R.id.btn_view_history);

        String disease = getIntent().getStringExtra("disease");
        String severity = getIntent().getStringExtra("severity");
        int severityScore = getIntent().getIntExtra("severityScore", 1);
        String description = getIntent().getStringExtra("description");
        long predictionTimestamp = getIntent().getLongExtra("predictionTimestamp", System.currentTimeMillis());

        String timeText = new SimpleDateFormat("dd MMM yyyy hh:mm a", Locale.US)
                .format(new Date(predictionTimestamp));

        tvDisease.setText(String.format(Locale.US, "Disease Name: %s", disease));
        tvSeverity.setText(String.format(Locale.US, "Severity: %s", severity));
        tvSeverityScore.setText(String.format(Locale.US,
                "Severity Score: %d/7 (%s)", severityScore, severityScore <= 3 ? "Low" : (severityScore == 4 ? "Medium" : "High")));
        tvPredictionTime.setText(String.format(Locale.US, "Prediction Time: %s", timeText));
        tvDescription.setText(String.format(Locale.US, "Disease Description: %s", description));

        PredictionHistoryStore historyStore = new PredictionHistoryStore(this);
        historyStore.addItem(new PredictionHistoryItem(disease, severity, severityScore, description, predictionTimestamp));

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
