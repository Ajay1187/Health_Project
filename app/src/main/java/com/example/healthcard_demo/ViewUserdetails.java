package com.example.healthcard_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;

public class ViewUserdetails extends AppCompatActivity {

    private TextView tvMedicalId, tvName, tvAddress, tvMobile, tvDob, tvAdhar,
            tvCurrentDisease, tvOldDisease, tvRecoveredDate, tvCurrentSymptoms;
    private TestAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_userdetails);

        tvMedicalId = findViewById(R.id.tv_medical_id_value);
        tvName = findViewById(R.id.tv_name_value);
        tvAddress = findViewById(R.id.tv_address_value);
        tvMobile = findViewById(R.id.tv_mobile_value);
        tvDob = findViewById(R.id.tv_dob_value);
        tvAdhar = findViewById(R.id.tv_adhar_value);
        tvCurrentDisease = findViewById(R.id.tv_current_disease_value);
        tvOldDisease = findViewById(R.id.tv_old_disease_value);
        tvRecoveredDate = findViewById(R.id.tv_recovered_date_value);
        tvCurrentSymptoms = findViewById(R.id.tv_current_symptoms_value);

        String medicalId = getIntent().getStringExtra("MedicalID");

        try {
            adapter = new TestAdapter(this);
            adapter.createDatabase();
            adapter.open();
            loadUserDetails(medicalId);
        } catch (Exception ignored) {
        }
    }

    private void loadUserDetails(String medicalId) {
        if (medicalId == null || medicalId.trim().isEmpty()) {
            return;
        }

        Cursor userCursor = adapter.getUserdetails(medicalId);
        if (userCursor != null && userCursor.moveToFirst()) {
            tvMedicalId.setText(getSafe(userCursor.getString(0)));
            tvName.setText(getSafe(userCursor.getString(1)));
            tvAddress.setText(getSafe(userCursor.getString(2)));
            tvMobile.setText(getSafe(userCursor.getString(3)));
            tvAdhar.setText(getSafe(userCursor.getString(4)));
            tvDob.setText(getSafe(userCursor.getString(5)));
            userCursor.close();
        }

        Cursor currentCursor = adapter.getLatestCurrentDisease(medicalId);
        if (currentCursor != null && currentCursor.moveToFirst()) {
            tvCurrentDisease.setText(getSafe(currentCursor.getString(2)));
            tvCurrentSymptoms.setText(getSafe(currentCursor.getString(3)));
            currentCursor.close();
        } else {
            tvCurrentDisease.setText("N/A");
            tvCurrentSymptoms.setText("N/A");
        }

        Cursor oldCursor = adapter.getLatestOldDisease(medicalId);
        if (oldCursor != null && oldCursor.moveToFirst()) {
            tvOldDisease.setText(getSafe(oldCursor.getString(2)));
            tvRecoveredDate.setText(getSafe(oldCursor.getString(1)));
            oldCursor.close();
        } else {
            tvOldDisease.setText("N/A");
            tvRecoveredDate.setText("N/A");
        }
    }

    private String getSafe(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "N/A";
        }
        return value;
    }
}
