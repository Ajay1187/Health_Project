package com.example.healthcard_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MedicalActivity extends AppCompatActivity {
Button byadhar,byid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical);
        byadhar=(Button) findViewById(R.id.btn_byadharcard);
        byid=(Button) findViewById(R.id.btn_bymedicalid);


        byid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(MedicalActivity.this,PatientMedicalHistory.class);
                startActivity(i);
            }
        });

        byadhar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(MedicalActivity.this,PatientMedicalbyadharcard.class);
                startActivity(i);
            }
        });
    }
}