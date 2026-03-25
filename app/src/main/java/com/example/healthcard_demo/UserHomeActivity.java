package com.example.healthcard_demo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class UserHomeActivity extends AppCompatActivity {
    private static final String TAG = "UserHomeActivity"; // Added TAG constant

    Button medicalhistory, profile, logout, addhealth, currentdisease, healthcard;
    String mobile;
    private long pressedTime;
    TextView tv1;
    TestAdapter adapter;
    String Medicalid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        medicalhistory = (Button) findViewById(R.id.btn_medicalhistory);
        profile = (Button) findViewById(R.id.btn_profile);
        logout = (Button) findViewById(R.id.btn_logout);
        addhealth = (Button) findViewById(R.id.btn_addhealthissue);
        currentdisease = (Button) findViewById(R.id.btn_predictdisease);
        healthcard = (Button) findViewById(R.id.btn_healthcard);
        tv1 = (TextView) findViewById(R.id.txt_id);

        try {
            adapter = new TestAdapter(this);
            adapter.createDatabase();
            adapter.open();
            Bundle bundle = getIntent().getExtras();
            mobile = bundle.getString("Key");

            Cursor c = adapter.getMedicalid(mobile);
            while (c.moveToNext()) {
                tv1.setText(c.getString(0));
            }

        } catch (Exception e) {
        }

        Medicalid = tv1.getText().toString();

        medicalhistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(UserHomeActivity.this, MedicalHistory.class);
                i.putExtra("MedicalID", Medicalid);
                startActivity(i);
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(UserHomeActivity.this, ViewUserProfile.class);
                i.putExtra("MedicalID", Medicalid);
                startActivity(i);
            }
        });

        healthcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(UserHomeActivity.this, HelathCard.class);
                i.putExtra("MedicalID", Medicalid);
                startActivity(i);
            }
        });


        addhealth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(UserHomeActivity.this, AddHealthissue.class);
                i.putExtra("MedicalID", Medicalid);
                startActivity(i);
            }
        });

        currentdisease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(UserHomeActivity.this, EnterSymptomsActivity.class);
                i.putExtra("MedicalID", Medicalid);
                startActivity(i);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UserHomeActivity.this);
                builder.setTitle("Confirmation PopUp!").
                        setMessage("You sure, that you want to logout?");
                builder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                SharedPreferences myPrefs = getSharedPreferences("MY",
                                        MODE_PRIVATE);
                                SharedPreferences.Editor editor = myPrefs.edit();
                                editor.clear();
                                editor.commit();
                                AppState.getSingleInstance().setLoggingOut(true);
                                Log.d(TAG, "Now log out and start the activity login");
                                Intent intent = new Intent(UserHomeActivity.this,
                                        LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);

                            }
                        });
                builder.setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert11 = builder.create();
                alert11.show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        new android.app.AlertDialog.Builder(this)
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
