package com.example.healthcard_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class DoctorForgetPassword extends AppCompatActivity {
    EditText mid,ans,pass,repass;
    Spinner sp1;
    Button b1;
    TestAdapter adapter;
    String id,question,sans,spass,srepass;
    String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{6,}$";

    List<String> list;
    ArrayAdapter<String> arrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_forget_password);

        mid=(EditText) findViewById(R.id.txt_forgetmedicaliddd);
        sp1=(Spinner) findViewById(R.id.spn_questionsdd);
        ans=(EditText) findViewById(R.id.txt_answerrrrdd);
        pass=(EditText) findViewById(R.id.txt_passnewdd);
        repass=(EditText) findViewById(R.id.txt_reenterpassdd);
        b1=(Button) findViewById(R.id.btn_flogindd);


        list = new ArrayList<String>();

        list.add("Select Security question");
        list.add("My first school name? ");
        list.add("My pet name?");
        list.add("My first laptop company name?");

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp1.setAdapter(arrayAdapter);

        try {

            adapter=new TestAdapter(this);
            adapter.createDatabase();
            adapter.open();


            b1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //id,question,sans,spass,srepass;

                    id=mid.getText().toString();
                    question=sp1.getSelectedItem().toString();
                    sans=ans.getText().toString();
                    spass=pass.getText().toString();
                    srepass=repass.getText().toString();

                    if (TextUtils.isEmpty(id)) {
                        Toast.makeText(getApplicationContext(), "Please Enter  Doctor License..", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(sans)) {
                        Toast.makeText(getApplicationContext(), "Please Enter Answer..", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (TextUtils.isEmpty(spass)) {
                        Toast.makeText(getApplicationContext(), "Please Enter  Password..", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(srepass)) {
                        Toast.makeText(getApplicationContext(), "Please Enter Re-Enter Password..", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (!spass.matches(PASSWORD_PATTERN)){
                        Toast.makeText(getApplicationContext(), "Invalid Password..", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!srepass.matches(PASSWORD_PATTERN)){
                        Toast.makeText(getApplicationContext(), "Invalid Password..", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (!srepass.matches(spass)) {
                        Toast.makeText(getApplicationContext(), "Password And Re-Enter Password Not Match..", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    else {
                        int i = adapter.checkDoctorandQuestions(id,question,sans);
                        if (i == 1) {

                            Toast.makeText(getApplicationContext(), "All Match..", Toast.LENGTH_SHORT).show();
                            passwordchange();
                            return;
                        }else {
                            Toast.makeText(getApplicationContext(), "Invalid Doctor License No OR Invalid Questions Answer..", Toast.LENGTH_SHORT).show();
                        }

                    }

                }
            });



        }catch (Exception e){}

    }

    private void passwordchange() {
        final ProgressDialog dialog =
                new ProgressDialog(DoctorForgetPassword.this);
        dialog.setIcon(R.drawable.add);
        dialog.setTitle("Processing Your Request...");
        dialog.setMessage("Please wait...");
        dialog.show();

        long i = adapter.Updatedoctor(id,spass);
        Toast.makeText(getApplicationContext(), " Password Updated..." + i, Toast.LENGTH_SHORT).show();

        final Runnable progressRunnable = new Runnable() {

            @Override
            public void run() {
                dialog.cancel();
                String mpass = null;

                final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.
                        Builder(DoctorForgetPassword.this);

                alertDialogBuilder.setTitle("Password Successfully Change..");

                final android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialogBuilder.setPositiveButton("OK.",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                Intent i = new Intent(DoctorForgetPassword.this, DoctorLogin.class);
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