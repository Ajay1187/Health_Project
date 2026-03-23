package com.example.healthcard_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class DoctorRegister extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    TextView tv1;
    EditText mid, name, address, phone,adhar,ans, password;
    Button register;
    TestAdapter adapter;
    String medicalid, semial, saddress, sphone,sadhar,squestion,sans, spassword;
    String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{6,}$";
    Spinner sp1;
    List<String> list;
    ArrayAdapter<String> arrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_register);

        mid = (EditText) findViewById(R.id.txt_medicalidd);
        name = (EditText) findViewById(R.id.txt_emaild);
        address = (EditText) findViewById(R.id.txt_addressd);
        phone = (EditText) findViewById(R.id.txt_phoned);
        adhar=(EditText)findViewById(R.id.txt_adharcardnod);
        password = (EditText) findViewById(R.id.txt_passwordd);
        register = (Button) findViewById(R.id.btn_pregisterd);
        tv1 = (TextView) findViewById(R.id.txt_alredyregisterd);
        sp1=(Spinner)findViewById(R.id.questionsd);
        ans=(EditText)findViewById(R.id.txt_answerd);

        list = new ArrayList<String>();

        list.add("Select Security question");
        list.add("My first school name? ");
        list.add("My pet name?");
        list.add("My first laptop company name?");

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp1.setAdapter(arrayAdapter);

        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DoctorRegister.this, DoctorLogin.class);
                startActivity(i);
            }
        });
        try {
            adapter = new TestAdapter(this);
            adapter.createDatabase();
            adapter.open();

            autoincrementid();

            register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    medicalid = mid.getText().toString();
                    semial = name.getText().toString();
                    saddress = address.getText().toString();
                    sphone = phone.getText().toString();
                    sadhar=adhar.getText().toString();
                    squestion=sp1.getSelectedItem().toString();
                    sans=ans.getText().toString();
                    spassword = password.getText().toString();

                    String mpass = null;
                    Cursor cursor = adapter.selectUser();
                    while (cursor.moveToNext()) {
                        mpass = cursor.getString(3).toString();
                        if (sphone.equalsIgnoreCase(mpass)) {

                            Toast.makeText(getApplicationContext(), "This Doctor is Already Registered..!", Toast.LENGTH_SHORT).show();
                            Log.w("5", "ok");
                            return;
                        }
                    }


                    if (TextUtils.isEmpty(medicalid)) {
                        Toast.makeText(getApplicationContext(), "Please Enter Doctor Name..", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(semial)) {
                        Toast.makeText(getApplicationContext(), "Please Enter Doctor Email Address..", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (TextUtils.isEmpty(saddress)) {
                        Toast.makeText(getApplicationContext(), "Please Enter Doctor Address..", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(sphone)) {
                        Toast.makeText(getApplicationContext(), "Please Enter Doctor  Mobile Number..", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (sphone.length() != 10) {
                        Toast.makeText(getApplicationContext(), "Please Enter 10 Digit Mobile Number.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (TextUtils.isEmpty(sadhar)) {
                        Toast.makeText(getApplicationContext(), "Please Enter Doctor License Number..", Toast.LENGTH_SHORT).show();
                        return;
                    }


                    if (TextUtils.isEmpty(sans)) {
                        Toast.makeText(getApplicationContext(), "Please Enter Security Questions Answer..", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (TextUtils.isEmpty(spassword)) {
                        Toast.makeText(getApplicationContext(), "Please Enter Password..", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!spassword.matches(PASSWORD_PATTERN)){
                        Toast.makeText(getApplicationContext(), "Invalid Password..", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    else {
                        RegisterUser();

                    }
                }
            });


        } catch (Exception e) {
        }


    }

    private void autoincrementid() {
        String number = " ";
        int no1;
        int flag = 0;
        Cursor cursor1 = adapter.selectdoctor();
        //cursor1.moveToFirst();
        while (cursor1.moveToNext()) {
            flag = 1;
        }
        cursor1.close();
        //setting id into edit text
        if (flag == 1) {
            try {
                Cursor cursor2 = adapter.doctorincrementid();
                while (cursor2.moveToNext()) {
                    if (cursor2.getString(0) != null)
                        number = cursor2.getString(0);

                }
                int n = Integer.parseInt(number);
                no1 = n + 1;
                mid.setText("" + no1);
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }

    private void RegisterUser() {

        final ProgressDialog dialog =
                new ProgressDialog(DoctorRegister.this);
        dialog.setIcon(R.drawable.add);
        dialog.setTitle("Processing Your Request...");
        dialog.setMessage("Please wait...");
        dialog.show();

        long i = adapter.InsertDoctor(medicalid, semial, saddress, sphone,sadhar,squestion,sans, spassword);
        // Toast.makeText(getApplicationContext(), " Register Sucessfully..." + i, Toast.LENGTH_SHORT).show();

        final Runnable progressRunnable = new Runnable() {

            @Override
            public void run() {
                dialog.cancel();
                String mpass = null;

                final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.
                        Builder(DoctorRegister.this);

                alertDialogBuilder.setTitle("Registration Successfully Done..");

                final android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialogBuilder.setPositiveButton("OK.",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                Intent i = new Intent(DoctorRegister.this, DoctorLogin.class);
                                startActivity(i);


                            }
                        });
                android.app.AlertDialog alDialog = alertDialogBuilder.create();
                alDialog.show();
            }

        };

        Handler pdCanceller = new Handler();
        pdCanceller.postDelayed(progressRunnable, 6000);

    }
}