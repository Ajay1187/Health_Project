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
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class AddHealthissue extends AppCompatActivity {

    private EditText mid;
    private EditText ddate;
    private Button b1;
    private TestAdapter adapter;
    private Spinner hdisease;
    private TextView symptomField;

    private String usermedicalid;

    private DiseaseDataRepository diseaseDataRepository;
    private List<String> diseases = new ArrayList<>();
    private List<String> availableSymptoms = new ArrayList<>();
    private final Set<String> selectedSymptoms = new LinkedHashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_healthissue);

        mid = findViewById(R.id.txt_medicalid);
        hdisease = findViewById(R.id.txt_diseses);
        symptomField = findViewById(R.id.txt_symtons);
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
            setupSymptomSelector();
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
                selectedSymptoms.clear();
                if (position == 0) {
                    availableSymptoms = new ArrayList<>();
                    symptomField.setText("Select Symptoms");
                    return;
                }
                String selectedDisease = diseaseChoices.get(position);
                availableSymptoms = diseaseDataRepository.getSymptomsForDisease(selectedDisease);
                symptomField.setText("Select Symptoms");
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                selectedSymptoms.clear();
                availableSymptoms = new ArrayList<>();
                symptomField.setText("Select Symptoms");
            }
        });
    }

    private void setupSymptomSelector() {
        symptomField.setOnClickListener(v -> {
            if (availableSymptoms.isEmpty()) {
                Toast.makeText(this, "Please select disease first.", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean[] checkedItems = new boolean[availableSymptoms.size()];
            for (int i = 0; i < availableSymptoms.size(); i++) {
                checkedItems[i] = selectedSymptoms.contains(availableSymptoms.get(i));
            }

            new AlertDialog.Builder(this)
                    .setTitle("Select Symptoms")
                    .setMultiChoiceItems(availableSymptoms.toArray(new String[0]), checkedItems, (dialog, which, isChecked) -> {
                        String symptom = availableSymptoms.get(which);
                        if (isChecked) {
                            selectedSymptoms.add(symptom);
                        } else {
                            selectedSymptoms.remove(symptom);
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Done", (dialog, which) -> {
                        if (selectedSymptoms.isEmpty()) {
                            symptomField.setText("Select Symptoms");
                        } else {
                            symptomField.setText(TextUtils.join(", ", selectedSymptoms));
                        }
                    })
                    .show();
        });
    }

    private void setupAddButton() {
        b1.setOnClickListener(view -> {
            String medicalid = mid.getText().toString().trim();
            String disdate = ddate.getText().toString().trim();
            String dname = hdisease.getSelectedItem() == null ? "" : hdisease.getSelectedItem().toString().trim();
            String sname = TextUtils.join(", ", selectedSymptoms);

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

            if (selectedSymptoms.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please select at least one symptom.", Toast.LENGTH_SHORT).show();
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
