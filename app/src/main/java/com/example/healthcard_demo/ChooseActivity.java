package com.example.healthcard_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ChooseActivity extends AppCompatActivity {
Button doctor,user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);


        doctor=(Button) findViewById(R.id.btn_doctorsignin);
         user=(Button)findViewById(R.id.btn_usersignin);

        doctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(ChooseActivity.this,DoctorLogin.class);
                startActivity(i);
            }
        });

        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(ChooseActivity.this,LoginActivity.class);
                startActivity(i);
            }
        });
    }
}