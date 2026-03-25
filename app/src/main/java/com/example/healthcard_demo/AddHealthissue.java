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

public class AddHealthissue extends AppCompatActivity {

    private EditText mid;
    private EditText ddate;
    private Button b1;
    private TestAdapter adapter;
    private Spinner hdisease;
    private Spinner msymtons;

    private String usermedicalid;

    private DiseaseDataRepository diseaseDataRepository;
    private List<String> diseases = new ArrayList<>();
    private List<String> currentSymptoms = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_healthissue);

        mid = findViewById(R.id.txt_medicalid);
        hdisease = findViewById(R.id.txt_diseses);
        msymtons = findViewById(R.id.txt_symtons);
        ddate = findViewById(R.id.txt_date);
        b1 = findViewById(R.id.btn_adddises);

        String dateNow = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(new Date());
        ddate.setText(dateNow);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            usermedicalid = bundle.getString("MedicalID", "");
            mid.setText(usermedicalid);
        }

        try {
            adapter = new TestAdapter(this);
            adapter.createDatabase();
            adapter.open();
            diseaseDataRepository = new DiseaseDataRepository(this);

            setupDiseaseSpinner();
            setupAddButton();
        } catch (Exception exception) {
            Toast.makeText(this, "Failed to load disease data.", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupDiseaseSpinner() {
        diseases = diseaseDataRepository.getAllDiseases();
        List<String> diseaseChoices = new ArrayList<>();
        diseaseChoices.add("Select Disease");
        diseaseChoices.addAll(diseases);

        ArrayAdapter<String> diseaseAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                diseaseChoices
        );
        diseaseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hdisease.setAdapter(diseaseAdapter);

        hdisease.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                if (position == 0) {
                    bindSymptoms(new ArrayList<>());
                    return;
                }
                String selectedDisease = diseaseChoices.get(position);
                bindSymptoms(diseaseDataRepository.getSymptomsForDisease(selectedDisease));
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                bindSymptoms(new ArrayList<>());
            }
        });

        bindSymptoms(new ArrayList<>());
    }

    private void bindSymptoms(List<String> symptoms) {
        currentSymptoms = new ArrayList<>(symptoms);
        List<String> symptomChoices = new ArrayList<>();
        symptomChoices.add("Select Symptoms");
        symptomChoices.addAll(currentSymptoms);

        ArrayAdapter<String> symptomAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                symptomChoices
        );
        symptomAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        msymtons.setAdapter(symptomAdapter);
    }

    private void setupAddButton() {
        b1.setOnClickListener(view -> {
            String medicalid = mid.getText().toString().trim();
            String disdate = ddate.getText().toString().trim();
            String dname = hdisease.getSelectedItem() == null ? "" : hdisease.getSelectedItem().toString().trim();
            String sname = msymtons.getSelectedItem() == null ? "" : msymtons.getSelectedItem().toString().trim();

            if (TextUtils.isEmpty(medicalid)) {
                Toast.makeText(getApplicationContext(), "Please Enter Your Medical Id..", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(disdate)) {
                Toast.makeText(getApplicationContext(), "Please Enter The Date..", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(dname) || "Select Disease".equalsIgnoreCase(dname)) {
                Toast.makeText(getApplicationContext(), "Please select disease.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(sname) || "Select Symptoms".equalsIgnoreCase(sname)) {
                Toast.makeText(getApplicationContext(), "Please select symptom.", Toast.LENGTH_SHORT).show();
                return;
            }

            showRecommendationAndConfirm(medicalid, disdate, dname, sname);
        });
    }

    private void showRecommendationAndConfirm(String medicalid, String disdate, String dname, String sname) {
        DiseaseResponse response = diseaseDataRepository.getDetailsForDisease(dname);
        String message = "Doctor Name: " + safe(response.getDoctorDetails()) + "\n\n"
                + "Diet Plan: " + safe(response.getDietPlan()) + "\n\n"
                + "Exercise: " + safe(response.getExercise());

        new AlertDialog.Builder(this)
                .setTitle("Suggested Plan")
                .setMessage(message)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Add Health Issue", (dialog, which) -> addHealthIssue(medicalid, disdate, dname, sname))
                .show();
    }

    private void addHealthIssue(String medicalid, String disdate, String dname, String sname) {
        ProgressDialog dialog = new ProgressDialog(AddHealthissue.this);
        dialog.setIcon(R.drawable.add);
        dialog.setTitle("Processing Your Request...");
        dialog.setMessage("Please wait...");
        dialog.show();

        long inserted = adapter.InsertHealthissue(medicalid, disdate, dname, sname);
        dialog.dismiss();

        if (inserted > 0) {
            new AlertDialog.Builder(AddHealthissue.this)
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
