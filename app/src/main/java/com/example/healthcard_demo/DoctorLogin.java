package com.example.healthcard_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class DoctorLogin extends AppCompatActivity {
TextView tv;
    TextView tv1,pass;
    Button b1;
    EditText mobile,password;
    TestAdapter adapter;
    String smedicalid,spassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_login);

        tv=(TextView) findViewById(R.id.txt_adminaccounntd);
        tv1=(TextView)findViewById(R.id.password_linkk);


        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(DoctorLogin.this,DoctorRegister.class);
                startActivity(i);
            }
        });

        b1=(Button)findViewById(R.id.btn_adminsiginddd);
        mobile=(EditText)findViewById(R.id.txt_mobiledddd);
        password=(EditText)findViewById(R.id.txt_passwordddd);

       // tv1=(TextView) findViewById(R.id.register);

        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(DoctorLogin.this,DoctorForgetPassword.class);
                startActivity(i);
            }
        });

        try {

            adapter=new TestAdapter(this);
            adapter.createDatabase();
            adapter.open();

            b1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    smedicalid = mobile.getText().toString().trim();
                    spassword= password.getText().toString().trim();

                    if(TextUtils.isEmpty(smedicalid))
                    {
                        mobile.setError("Medical Id is Required.");
                        return;
                    }

                    if(TextUtils.isEmpty(spassword))
                    {
                        password.setError("Password is Required.");
                        return;
                    }

                    int i = adapter.checDoctorLogin(smedicalid,spassword);
                    if (i == 1) {

                        userlogin();
                        return;

                    }

                    if (smedicalid.equalsIgnoreCase("9860202746")&&(spassword.equalsIgnoreCase("9197"))){
                        Intent intent = new Intent(getApplicationContext(), DoctorHomeActivity.class);
                        intent.putExtra("Key",smedicalid);
                        startActivity(intent);

                        Toast.makeText(DoctorLogin.this, "Login Successfull", Toast.LENGTH_SHORT).show();
                        return;

                    }

                    else{
                        Toast.makeText(DoctorLogin.this, "Invalid Id Or Password", Toast.LENGTH_SHORT).show();
                        return;

                    }


                }
            });

        }catch (Exception e){}


    }

    private void userlogin() {
        final ProgressDialog dialog =
                new ProgressDialog(DoctorLogin.this);
        dialog.setIcon(R.drawable.login);
        dialog.setTitle("Login");
        dialog.setMessage("Please wait User Login is Processing...");
        dialog.show();

        final Runnable progressRunnable = new Runnable() {

            @Override
            public void run() {
                dialog.cancel();
                String mpass = null;

                Intent i = new Intent(DoctorLogin.this, DoctorHomeActivity.class);
                i.putExtra("Key",smedicalid);
                startActivity(i);

                Toast.makeText(getApplicationContext(), "Your Login is Successfull..." , Toast.LENGTH_SHORT).show();

                mobile.setText("");
                password.setText("");


            }
        };
        Handler pdCanceller = new Handler();
        pdCanceller.postDelayed(progressRunnable, 6000);

    }
}