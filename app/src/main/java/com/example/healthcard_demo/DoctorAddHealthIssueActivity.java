package com.example.healthcard_demo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DoctorAddHealthIssueActivity extends AppCompatActivity {

    private EditText doctorIdField;
    private EditText patientMedicalIdField;
    private EditText dateField;
    private Spinner diseaseSpinner;
    private Button addIssueButton;

    private TestAdapter adapter;
    private DiseaseDataRepository diseaseDataRepository;
    private List<String> diseaseChoices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_add_healthissue);

        doctorIdField = findViewById(R.id.txt_doctor_id);
        patientMedicalIdField = findViewById(R.id.txt_patient_medical_id);
        dateField = findViewById(R.id.txt_doctor_date);
        diseaseSpinner = findViewById(R.id.spn_doctor_disease);
        addIssueButton = findViewById(R.id.btn_doctor_add_issue);

        String dateNow = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(new Date());
        dateField.setText(dateNow);

        String doctorId = getIntent().getStringExtra("DoctorID");
        doctorIdField.setText(doctorId == null ? "" : doctorId);

        try {
            adapter = new TestAdapter(this);
            adapter.createDatabase();
            adapter.open();

            diseaseDataRepository = new DiseaseDataRepository(this);
            setupDiseaseSpinner();
            setupAddButton();
        } catch (Exception exception) {
            Toast.makeText(this, "Unable to load doctor add health issue module.", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupDiseaseSpinner() {
        List<String> diseases = diseaseDataRepository.getAllDiseases();
        diseaseChoices = new ArrayList<>();
        diseaseChoices.add("Select Disease");
        diseaseChoices.addAll(diseases);

        ArrayAdapter<String> diseaseAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                diseaseChoices
        );
        diseaseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        diseaseSpinner.setAdapter(diseaseAdapter);
    }

    private void setupAddButton() {
        addIssueButton.setOnClickListener(v -> {
            String doctorId = doctorIdField.getText().toString().trim();
            String patientMedicalId = patientMedicalIdField.getText().toString().trim();
            String date = dateField.getText().toString().trim();
            String disease = diseaseSpinner.getSelectedItem() == null ? "" : diseaseSpinner.getSelectedItem().toString().trim();

            if (TextUtils.isEmpty(doctorId)) {
                Toast.makeText(this, "Doctor ID is required.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(patientMedicalId)) {
                Toast.makeText(this, "Please enter patient medical ID.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(disease) || "Select Disease".equalsIgnoreCase(disease)) {
                Toast.makeText(this, "Please select disease.", Toast.LENGTH_SHORT).show();
                return;
            }

            List<String> diseaseSymptoms = diseaseDataRepository.getSymptomsForDisease(disease);
            String symptoms = diseaseSymptoms.isEmpty() ? "Not specified" : TextUtils.join(", ", diseaseSymptoms);

            showRecommendationAndConfirm(patientMedicalId, date, disease, symptoms);
        });
    }

    private void showRecommendationAndConfirm(String patientMedicalId, String date, String disease, String symptoms) {
        DiseaseResponse response = diseaseDataRepository.getDetailsForDisease(disease);
        String message = "Doctor Name: " + safe(response.getDoctorDetails()) + "\n\n"
                + "Diet Plan: " + safe(response.getDietPlan()) + "\n\n"
                + "Exercise: " + safe(response.getExercise());

        new AlertDialog.Builder(this)
                .setTitle("Suggested Plan")
                .setMessage(message)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Add Health Issue", (dialog, which) -> addHealthIssue(patientMedicalId, date, disease, symptoms))
                .show();
    }

    private void addHealthIssue(String patientMedicalId, String date, String disease, String symptoms) {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setIcon(R.drawable.add);
        dialog.setTitle("Processing Your Request...");
        dialog.setMessage("Please wait...");
        dialog.show();

        long inserted = adapter.InsertHealthissue(patientMedicalId, date, disease, symptoms);
        dialog.dismiss();

        if (inserted > 0) {
            new AlertDialog.Builder(this)
                    .setTitle("Disease Added Successfully")
                    .setMessage("The issue was added to Current Disease history.")
                    .setPositiveButton("OK", null)
                    .show();
        } else {
            Toast.makeText(this, "Failed to add health issue.", Toast.LENGTH_SHORT).show();
        }
    }

    private String safe(String value) {
        return TextUtils.isEmpty(value) ? "Not available" : value;
    }
}
