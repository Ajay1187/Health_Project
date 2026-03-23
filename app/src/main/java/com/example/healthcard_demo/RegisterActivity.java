package com.example.healthcard_demo;

import static com.example.healthcard_demo.TestAdapter.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    TextView tv1;
    EditText mid, name, address, phone,adhar,bdate,ans, password;
    Button register;
    TestAdapter adapter;
    String medicalid, semial, saddress, sphone,sadhar,sbdate,squestion,sans, spassword;
    String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{6,}$";
    Spinner sp1;
    List<String> list;
    ArrayAdapter<String> arrayAdapter;
    private final int MY_PERMISSIONS_REQUEST=100;
    String phoneNo,message;
    Calendar myCalendar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mid = (EditText) findViewById(R.id.txt_medicalid);
        name = (EditText) findViewById(R.id.txt_name);
        address = (EditText) findViewById(R.id.txt_address);
        phone = (EditText) findViewById(R.id.txt_phone);
        adhar=(EditText)findViewById(R.id.txt_adharcardno);
        password = (EditText) findViewById(R.id.txt_password);
        register = (Button) findViewById(R.id.btn_pregister);
        tv1 = (TextView) findViewById(R.id.txt_alredyregisterd);
        sp1=(Spinner)findViewById(R.id.questions);
        ans=(EditText)findViewById(R.id.txt_answer);
        bdate=(EditText)findViewById(R.id.txt_dateofbirth);


        bdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();

                // on below line we are getting
                // our day, month and year.
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        // on below line we are passing context.
                        RegisterActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // on below line we are setting date to our edit text.
                                bdate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                            }
                        },
                        // on below line we are passing year,
                        // month and day for selected date in our date picker.
                        year, month, day);
                // at last we are calling show to
                // display our date picker dialog.
                datePickerDialog.show();
            }
        });







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
                Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
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
                    sbdate=bdate.getText().toString();
                    squestion=sp1.getSelectedItem().toString();
                    sans=ans.getText().toString();
                    spassword = password.getText().toString();

                    String mpass = null;

                    Cursor cursor = adapter.selectUser();
                    while (cursor.moveToNext()) {
                        mpass = cursor.getString(3).toString();
                        if (sphone.equalsIgnoreCase(mpass)) {

                            Toast.makeText(getApplicationContext(), "This User is Already Registered..!", Toast.LENGTH_SHORT).show();
                            Log.w("5", "ok");
                            return;
                        }
                    }

                        String ano=null;
                        Cursor c = adapter.userselect();
                        while (c.moveToNext()) {
                            ano = c.getString(4).toString();
                            if (sadhar.equalsIgnoreCase(ano)) {

                                Toast.makeText(getApplicationContext(), "This Adhar Card No is Already Registered..!", Toast.LENGTH_SHORT).show();
                                Log.w("5", "ok");
                                return;
                            }

                        }



                    if (TextUtils.isEmpty(medicalid)) {
                        Toast.makeText(getApplicationContext(), "Please Enter User Name..", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(semial)) {
                        Toast.makeText(getApplicationContext(), "Please Enter Email Address..", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (TextUtils.isEmpty(saddress)) {
                        Toast.makeText(getApplicationContext(), "Please Enter  Address..", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(sphone)) {
                        Toast.makeText(getApplicationContext(), "Please Enter  Mobile Number..", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (sphone.length() != 10) {
                        Toast.makeText(getApplicationContext(), "Please Enter 10 Digit Mobile Number.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (TextUtils.isEmpty(sadhar)) {
                        Toast.makeText(getApplicationContext(), "Please Enter Adhar Card Number..", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (sadhar.length() != 12) {
                        Toast.makeText(getApplicationContext(), "Please Enter Valid Adhar Card Number 12 Digit.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (TextUtils.isEmpty(sbdate)) {
                        Toast.makeText(getApplicationContext(), "Please Enter Birth Date..", Toast.LENGTH_SHORT).show();
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
        Cursor cursor1 = adapter.selectUser();
        //cursor1.moveToFirst();
        while (cursor1.moveToNext()) {
            flag = 1;
        }
        cursor1.close();
        //setting id into edit text
        if (flag == 1) {
            try {
                Cursor cursor2 = adapter.userincrementid();
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
                new ProgressDialog(RegisterActivity.this);
        dialog.setIcon(R.drawable.add);
        dialog.setTitle("Processing Your Request...");
        dialog.setMessage("Please wait...");
        dialog.show();

        long i = adapter.Insertuser(medicalid, semial, saddress, sphone,sadhar,sbdate,squestion,sans, spassword);
        // Toast.makeText(getApplicationContext(), " Register Sucessfully..." + i, Toast.LENGTH_SHORT).show();

        final Runnable progressRunnable = new Runnable() {

            @Override
            public void run() {
                dialog.cancel();
                String mpass = null;

                final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.
                        Builder(RegisterActivity.this);

                alertDialogBuilder.setTitle("Your Registration is Successful..");
                alertDialogBuilder.setMessage("Your Medical Id Is  "+medicalid+
                        "\nPlease Save Your Id");

                final android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialogBuilder.setPositiveButton("OK.",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                //sendSMSMessage();
                                // TextView textView = findViewById(R.id.number_to_call);
                                String smsNumber = String.format("smsto: %s", phone.getText().toString());
                                //  EditText smsEditText = findViewById(R.id.sms_message);
                                String sms = mid.getText().toString();
                                Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
                                smsIntent.setData(Uri.parse(smsNumber));
                                smsIntent.putExtra("sms_body", sms);
                                if (smsIntent.resolveActivity(getPackageManager()) != null) {
                                    startActivity(smsIntent);
                                } else {
                                    Log.d(TAG, "Can't resolve app for ACTION_SENDTO Intent");
                                }
                                Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
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

    private void sendSMSMessage() {
        phoneNo = phone.getText().toString();
        message = mid.getText().toString();

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneNo, null, message, null, null);
                    Toast.makeText(getApplicationContext(), "SMS sent.",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "SMS faild, please try again.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }

    }


}
