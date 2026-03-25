package com.example.healthcard_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class PatientMedicalbyadharcard extends AppCompatActivity {
    ListView sp1;
    ArrayAdapter<String> ad;
    List<String> list;
    EditText inputSearch;
    TestAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_medicalbyadharcard);
        sp1 = findViewById(R.id.txt_bypatientmedicalid);
        inputSearch = findViewById(R.id.byinputSearch);

        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                PatientMedicalbyadharcard.this.ad.getFilter().filter(cs);
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

            addAdharId();

            sp1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    String adharId = sp1.getItemAtPosition(position).toString();
                    String medicalId = getMedicalIdByAdhar(adharId);
                    if (medicalId == null) {
                        return;
                    }
                    Intent intent = new Intent(PatientMedicalbyadharcard.this, ViewUserdetails.class);
                    intent.putExtra("MedicalID", medicalId);
                    startActivity(intent);
                }
            });

        } catch (Exception ignored) {
        }
    }

    private void addAdharId() {
        Cursor c = adapter.selectUser();
        list = new ArrayList<>();
        while (c.moveToNext()) {
            list.add(c.getString(4));
        }
        ad = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, list);
        sp1.setAdapter(ad);
    }

    private String getMedicalIdByAdhar(String adharId) {
        Cursor cursor = adapter.getalluserdetails(adharId);
        if (cursor == null || !cursor.moveToFirst()) {
            return null;
        }
        String medicalId = cursor.getString(0);
        cursor.close();
        return medicalId;
    }
}
