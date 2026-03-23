package com.example.healthcard_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class DoctorProfile extends AppCompatActivity {
    Button b1;
    String medicalid;
    TestAdapter adapter;
    EditText mid, name, address, phone, password;
    String sphone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_profile);
        mid = (EditText) findViewById(R.id.txt_drpmid);
        name = (EditText) findViewById(R.id.txt_drpname);
        address = (EditText) findViewById(R.id.txt_drpaddress);
        phone = (EditText) findViewById(R.id.txt_drpphone);
        b1=(Button) findViewById(R.id.btn_drbackk);



        try {
            adapter = new TestAdapter(this);
            adapter.createDatabase();
            adapter.open();



            Bundle bundle = getIntent().getExtras();
            medicalid = bundle.getString("MedicalID");

            Cursor c=adapter.getDoctorid(medicalid);
            while (c.moveToNext()){
                mid.setText(c.getString(0));
                name.setText(c.getString(1));
                address.setText(c.getString(2));
                phone.setText(c.getString(3));
                // password.setText(c.getString(4));

            }


        }catch (Exception e){}


        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(DoctorProfile.this,DoctorHomeActivity.class);
                startActivity(i);
            }
        });


    }
}