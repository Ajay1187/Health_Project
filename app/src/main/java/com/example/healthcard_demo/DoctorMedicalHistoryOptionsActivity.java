package com.example.healthcard_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class DoctorMedicalHistoryOptionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_medical_history_options);

        Button byAdhar = findViewById(R.id.btn_by_adhar_card);
        Button byMedicalId = findViewById(R.id.btn_by_medical_id);

        byAdhar.setOnClickListener(v ->
                startActivity(new Intent(DoctorMedicalHistoryOptionsActivity.this, PatientMedicalbyadharcard.class)));

        byMedicalId.setOnClickListener(v ->
                startActivity(new Intent(DoctorMedicalHistoryOptionsActivity.this, PatientMedicalHistory.class)));
    }
}
