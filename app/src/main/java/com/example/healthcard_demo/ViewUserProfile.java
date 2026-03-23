package com.example.healthcard_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ViewUserProfile extends AppCompatActivity {

    Button b1;
    String medicalid;
    TestAdapter adapter;
    EditText mid, name, address, phone, password;
    String sphone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user_profile);


        mid = (EditText) findViewById(R.id.txt_pmid);
        name = (EditText) findViewById(R.id.txt_pname);
        address = (EditText) findViewById(R.id.txt_paddress);
        phone = (EditText) findViewById(R.id.txt_pphone);
      //  password = (EditText) findViewById(R.id.txt_ppassword);
       b1=(Button) findViewById(R.id.btn_backk);

        try {
            adapter = new TestAdapter(this);
            adapter.createDatabase();
            adapter.open();



            Bundle bundle = getIntent().getExtras();
            medicalid = bundle.getString("MedicalID");

            Cursor c=adapter.getUserdetails(medicalid);
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
        Intent i=new Intent(ViewUserProfile.this,UserHomeActivity.class);
        startActivity(i);
    }
});


    }
}