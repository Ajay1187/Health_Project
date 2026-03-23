package com.example.healthcard_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class HealthInsuranceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_insurance);

        String medicalId = getIntent().getStringExtra("MedicalID");
        HealthInsuranceDocument document = HealthInsuranceManager.getOrCreateDocument(this, medicalId);

        ((TextView) findViewById(R.id.tv_policy_number)).setText(document.getPolicyNumber());
        ((TextView) findViewById(R.id.tv_provider)).setText(document.getProviderName());
        ((TextView) findViewById(R.id.tv_plan)).setText(document.getPlanName());
        ((TextView) findViewById(R.id.tv_insured_sum)).setText(document.getSumInsured());
        ((TextView) findViewById(R.id.tv_premium)).setText(document.getPremium());
        ((TextView) findViewById(R.id.tv_validity)).setText(document.getValidTill());
        ((TextView) findViewById(R.id.tv_support)).setText(document.getSupportNumber());
    }
}
