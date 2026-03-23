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
import android.widget.Toast;

public class DoctorHomeActivity extends AppCompatActivity {
Button b1,b2,logout,aboutus;
TextView tv;
TestAdapter adapter;
String drid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_home);
        b1=(Button) findViewById(R.id.btn_drmedical);
        b2=(Button)findViewById(R.id.btn_drprofile);
        tv=(TextView)findViewById(R.id.txt_drid);
        logout=(Button) findViewById(R.id.btn_drlogout);
        aboutus=(Button) findViewById(R.id.btn_draboutus);


        try {

            adapter = new TestAdapter(this);
            adapter.createDatabase();
            adapter.open();

            Bundle bundle = getIntent().getExtras();
            drid = bundle.getString("Key");
            Cursor c=adapter.getDoctorid(drid);
            while (c.moveToNext()){
                tv.setText(c.getString(0));
            }


        }catch (Exception e){}


        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(DoctorHomeActivity.this,MedicalActivity.class);
                startActivity(i);
            }
        });

        aboutus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(DoctorHomeActivity.this,DoctorAboutus.class);
                startActivity(i);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DoctorHomeActivity.this);

                // Set the message show for the Alert time
                builder.setMessage("Do you want to Logout ?");

                // Set Alert Title
                builder.setTitle("Alert !");

                // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
                builder.setCancelable(false);

                // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
                builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
                    // When the user click yes button then app will close
                    Intent i=new Intent(DoctorHomeActivity.this,ChooseActivity.class);
                    startActivity(i);
                });

                // Set the Negative button with No name Lambda OnClickListener method is use of DialogInterface interface.
                builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
                    // If user click no then dialog box is canceled.
                    dialog.cancel();
                });

                AlertDialog alertDialog = builder.create();
                // Show the Alert Dialog box
                alertDialog.show();
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(DoctorHomeActivity.this,DoctorProfile.class);
                i.putExtra("MedicalID",drid);
                startActivity(i);
            }
        });




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