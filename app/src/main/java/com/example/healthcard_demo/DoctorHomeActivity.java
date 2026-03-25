package com.example.healthcard_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DoctorHomeActivity extends AppCompatActivity {
Button b2, logout, aboutus, medicalHistory;
TextView tv;
TestAdapter adapter;
String drid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_home);
        b2 = findViewById(R.id.btn_drprofile);
        tv = findViewById(R.id.txt_drid);
        logout = findViewById(R.id.btn_drlogout);
        aboutus = findViewById(R.id.btn_draboutus);
        medicalHistory = findViewById(R.id.btn_drmedicalhistory);

        try {
            adapter = new TestAdapter(this);
            adapter.createDatabase();
            adapter.open();

            Bundle bundle = getIntent().getExtras();
            drid = bundle.getString("Key");
            Cursor c = adapter.getDoctorid(drid);
            while (c.moveToNext()) {
                tv.setText(c.getString(0));
            }

        } catch (Exception ignored) {
        }

        aboutus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DoctorHomeActivity.this, DoctorAboutus.class);
                startActivity(i);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DoctorHomeActivity.this);
                builder.setMessage("Do you want to Logout ?");
                builder.setTitle("Alert !");
                builder.setCancelable(false);

                builder.setPositiveButton("Yes", (dialog, which) -> {
                    Intent i = new Intent(DoctorHomeActivity.this, ChooseActivity.class);
                    startActivity(i);
                });

                builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DoctorHomeActivity.this, DoctorProfile.class);
                i.putExtra("MedicalID", drid);
                startActivity(i);
            }
        });

        medicalHistory.setOnClickListener(view ->
                startActivity(new Intent(DoctorHomeActivity.this, DoctorMedicalHistoryOptionsActivity.class)));
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finishAffinity();
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}
