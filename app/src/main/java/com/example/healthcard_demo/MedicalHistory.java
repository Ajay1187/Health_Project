package com.example.healthcard_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TableLayout;

public class MedicalHistory extends AppCompatActivity {

    Button btnCurrent, btnOld;
    TableLayout layout;
    TestAdapter adapter;
    String usermedicalid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_history);

        btnCurrent = findViewById(R.id.btn_current);
        btnOld = findViewById(R.id.btn_old);
        layout = findViewById(R.id.table_tblecurrentdisease);

        try {
            adapter = new TestAdapter(this);
            adapter.createDatabase();
            adapter.open();

            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                usermedicalid = bundle.getString("MedicalID");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // ✅ CURRENT DISEASE BUTTON → NewMedicalHistory Activity
        btnCurrent.setOnClickListener(v -> {
            Intent intent = new Intent(MedicalHistory.this, NewMedicalHistory.class);
            intent.putExtra("MedicalID", usermedicalid);
            startActivity(intent);
        });

        // ✅ OLD DISEASE BUTTON → OldMedicalHistory Activity
        btnOld.setOnClickListener(v -> {
            Intent intent = new Intent(MedicalHistory.this, OldMedicalHistory.class);
            intent.putExtra("MedicalID", usermedicalid);
            startActivity(intent);
        });
    }
}