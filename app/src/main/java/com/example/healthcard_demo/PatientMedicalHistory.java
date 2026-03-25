package com.example.healthcard_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class PatientMedicalHistory extends AppCompatActivity {
    ListView sp1;
    ArrayAdapter<String> ad;
    List<String> list;
    EditText inputSearch;
    TestAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_medical_history);

        sp1 = findViewById(R.id.txt_patientmedicalid);
        inputSearch = findViewById(R.id.inputSearch);

        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                PatientMedicalHistory.this.ad.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }
        });

        try {
            adapter = new TestAdapter(this);
            adapter.createDatabase();
            adapter.open();

            addMedicalId();

            sp1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    String medicalId = sp1.getItemAtPosition(position).toString();
                    Intent intent = new Intent(PatientMedicalHistory.this, ViewUserdetails.class);
                    intent.putExtra("MedicalID", medicalId);
                    startActivity(intent);
                }
            });

        } catch (Exception ignored) {
        }
    }

    private void addMedicalId() {
        Cursor c = adapter.selectUser();
        list = new ArrayList<>();
        while (c.moveToNext()) {
            list.add(c.getString(0));
        }
        ad = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, list);
        sp1.setAdapter(ad);
    }
}
