package com.example.healthcard_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
Button b1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_main);

      //  b1=(Button) findViewById(R.id.btn_start);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        // on below line we are calling handler to run a task
        // for specific time interval
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // on below line we are
                // creating a new intent
                Intent i = new Intent(MainActivity.this, ChooseActivity.class);

                // on below line we are
                // starting a new activity.
                startActivity(i);

                // on the below line we are finishing
                // our current activity.
                finish();
            }
        }, 2000);
    }

     /*   b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog dialog =
                        new ProgressDialog(MainActivity.this);
                dialog.setIcon(R.drawable.login);
                dialog.setTitle("START HEALTH CARE SYSTEM");
                dialog.setMessage("Loading Please Wait....");
                dialog.show();

                final Runnable progressRunnable = new Runnable() {

                    @Override
                    public void run() {
                        dialog.cancel();
                        String mpass = null;

                        Intent i = new Intent(MainActivity.this, ChooseActivity.class);
                        startActivity(i);

                        Toast.makeText(getApplicationContext(), "Welcome to Health Care System Application..." , Toast.LENGTH_SHORT).show();

                    }
                };
                Handler pdCanceller = new Handler();
                pdCanceller.postDelayed(progressRunnable, 1000);


            }
        });*/
    }
