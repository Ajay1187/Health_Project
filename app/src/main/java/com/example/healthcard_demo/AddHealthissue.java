package com.example.healthcard_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddHealthissue extends AppCompatActivity {
EditText mid,ddate;
Button b1;
TestAdapter adapter;
String medicalid,disdate,dname,sname;
String usermedicalid;
Spinner hdisease,msymtons;
    List<String> list,list1;
    ArrayAdapter<String> arrayAdapter;
String mobile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_healthissue);
        mid=(EditText) findViewById(R.id.txt_medicalid);
        hdisease=(Spinner) findViewById(R.id.txt_diseses);
        msymtons=(Spinner) findViewById(R.id.txt_symtons);
        ddate=(EditText)findViewById(R.id.txt_date) ;
        b1=(Button) findViewById(R.id.btn_adddises);

        list = new ArrayList<String>();

        list.add("itching");
        list.add("skin_rash ");
        list.add("nodal_skin_eruptions");
        list.add("Cephalalgia");

        list.add("Rhinovirus");
        list.add("Otitis externa ");
        list.add("Pharyngitis");
        list.add("Tussis");

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hdisease.setAdapter(arrayAdapter);


        list1 = new ArrayList<String>();
        list1.add("Select Symptoms");
        list1.add("loss of appetite");
        list1.add("facial pain ");
        list1.add("sore throat");
        list1.add("cough, and congestion.");

        list1.add("Ritch in ear, ear pain, pus in ear");
        list1.add(" fluids, gargling ");
        list1.add("swelling in the neck");
        list1.add("changes in the voice and difficulty breathing");


        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list1);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        msymtons.setAdapter(arrayAdapter);


        String date_n = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(new Date());
      ;
        ddate.setText(date_n);

        try {
            adapter = new TestAdapter(this);
            adapter.createDatabase();
            adapter.open();


            Bundle bundle = getIntent().getExtras();
            usermedicalid = bundle.getString("MedicalID");
            mid.setText(usermedicalid);

            b1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    medicalid=mid.getText().toString();
                    disdate=ddate.getText().toString();
                    dname=hdisease.getSelectedItem().toString();
                    sname=msymtons.getSelectedItem().toString();


                    if (TextUtils.isEmpty(medicalid)) {
                        Toast.makeText(getApplicationContext(), "Please Enter Your Medical Id..", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (TextUtils.isEmpty(disdate)) {
                        Toast.makeText(getApplicationContext(), "Please Enter The Date..", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(dname)) {
                        Toast.makeText(getApplicationContext(), "Please Enter The Disease Name..", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (TextUtils.isEmpty(sname)) {
                        Toast.makeText(getApplicationContext(), "Please Enter  the Symptoms..", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else {

                        AddHelthissue();

                    }

                }
            });

            msymtons.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if( i==0){

                    }
                    if(i==1){
                        final ProgressDialog dialog =
                                new ProgressDialog(AddHealthissue.this);
                        dialog.setIcon(R.drawable.add);
                        dialog.setTitle("Prediction Process Is Start...");
                        dialog.setMessage("Please wait...");
                        dialog.show();

                        //   long i = adapter.InsertHealthissue(medicalid,disdate, dname, sname);
                        //Toast.makeText(getApplicationContext(), "Health Issue Register Sucessfully..." + i, Toast.LENGTH_SHORT).show();

                        final Runnable progressRunnable = new Runnable() {

                            @Override
                            public void run() {
                                dialog.cancel();
                                String mpass = null;

                                final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.
                                        Builder(AddHealthissue.this);

                                alertDialogBuilder.setMessage("Suggest Doctor :- Chetan Deskmukh\nDiet Plan:- Fruits\nExercise:- Yoga ..");

                                final android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialogBuilder.setPositiveButton("OK.",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface arg0, int arg1) {




                                            }
                                        });
                                android.app.AlertDialog alDialog = alertDialogBuilder.create();
                                alDialog.show();
                            }

                        };

                        Handler pdCanceller = new Handler();
                        pdCanceller.postDelayed(progressRunnable, 3000);
                    }



                    if(i==2){
                        final ProgressDialog dialog =
                                new ProgressDialog(AddHealthissue.this);
                        dialog.setIcon(R.drawable.add);
                        dialog.setTitle("Prediction Process Is Start...");
                        dialog.setMessage("Please wait...");
                        dialog.show();

                        //   long i = adapter.InsertHealthissue(medicalid,disdate, dname, sname);
                        //Toast.makeText(getApplicationContext(), "Health Issue Register Sucessfully..." + i, Toast.LENGTH_SHORT).show();

                        final Runnable progressRunnable = new Runnable() {

                            @Override
                            public void run() {
                                dialog.cancel();
                                String mpass = null;

                                final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.
                                        Builder(AddHealthissue.this);

                                alertDialogBuilder.setMessage("Suggest Doctor :- Dr.Kishor Mane\n" +
                                        "Diet Plan:- The Paleo Diet\n" +
                                        "Exercise:- Gym ..");

                                final android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialogBuilder.setPositiveButton("OK.",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface arg0, int arg1) {


                                            }
                                        });
                                android.app.AlertDialog alDialog = alertDialogBuilder.create();
                                alDialog.show();
                            }

                        };

                        Handler pdCanceller = new Handler();
                        pdCanceller.postDelayed(progressRunnable, 3000);
                    }

                    if(i==3){
                        final ProgressDialog dialog =
                                new ProgressDialog(AddHealthissue.this);
                        dialog.setIcon(R.drawable.add);
                        dialog.setTitle("Prediction Process Is Start...");
                        dialog.setMessage("Please wait...");
                        dialog.show();

                        //   long i = adapter.InsertHealthissue(medicalid,disdate, dname, sname);
                        //Toast.makeText(getApplicationContext(), "Health Issue Register Sucessfully..." + i, Toast.LENGTH_SHORT).show();

                        final Runnable progressRunnable = new Runnable() {

                            @Override
                            public void run() {
                                dialog.cancel();
                                String mpass = null;

                                final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.
                                        Builder(AddHealthissue.this);

                                alertDialogBuilder.setMessage("Suggest Doctor :- Dr.Subhodh Shivde\n" +
                                        "Diet Plan:-The Vegan Diet.\n" +
                                        "Exercise:- Runing ..");

                                final android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialogBuilder.setPositiveButton("OK.",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface arg0, int arg1) {




                                            }
                                        });
                                android.app.AlertDialog alDialog = alertDialogBuilder.create();
                                alDialog.show();
                            }

                        };

                        Handler pdCanceller = new Handler();
                        pdCanceller.postDelayed(progressRunnable, 3000);
                    }

                    if(i==4){
                        final ProgressDialog dialog =
                                new ProgressDialog(AddHealthissue.this);
                        dialog.setIcon(R.drawable.add);
                        dialog.setTitle("Prediction Process Is Start...");
                        dialog.setMessage("Please wait...");
                        dialog.show();

                        //   long i = adapter.InsertHealthissue(medicalid,disdate, dname, sname);
                        //Toast.makeText(getApplicationContext(), "Health Issue Register Sucessfully..." + i, Toast.LENGTH_SHORT).show();

                        final Runnable progressRunnable = new Runnable() {

                            @Override
                            public void run() {
                                dialog.cancel();
                                String mpass = null;

                                final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.
                                        Builder(AddHealthissue.this);

                                alertDialogBuilder.setMessage("Suggest Doctor :- Dr.Prakash Kadam\n" +
                                        "Diet Plan:-Low-Carb Diets.\n" +
                                        "Exercise:- Walk 3km ..");

                                final android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialogBuilder.setPositiveButton("OK.",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface arg0, int arg1) {




                                            }
                                        });
                                android.app.AlertDialog alDialog = alertDialogBuilder.create();
                                alDialog.show();
                            }

                        };

                        Handler pdCanceller = new Handler();
                        pdCanceller.postDelayed(progressRunnable, 3000);
                    }


                    if(i==5){
                        final ProgressDialog dialog =
                                new ProgressDialog(AddHealthissue.this);
                        dialog.setIcon(R.drawable.add);
                        dialog.setTitle("Prediction Process Is Start...");
                        dialog.setMessage("Please wait...");
                        dialog.show();

                        //   long i = adapter.InsertHealthissue(medicalid,disdate, dname, sname);
                        //Toast.makeText(getApplicationContext(), "Health Issue Register Sucessfully..." + i, Toast.LENGTH_SHORT).show();

                        final Runnable progressRunnable = new Runnable() {

                            @Override
                            public void run() {
                                dialog.cancel();
                                String mpass = null;

                                final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.
                                        Builder(AddHealthissue.this);

                                alertDialogBuilder.setMessage("Suggest Doctor :- Dr.Sumit Kadam\n" +
                                        "Diet Plan:-The Dukan Diet.\n" +
                                        "Exercise:- Yoga ..");

                                final android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialogBuilder.setPositiveButton("OK.",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface arg0, int arg1) {




                                            }
                                        });
                                android.app.AlertDialog alDialog = alertDialogBuilder.create();
                                alDialog.show();
                            }

                        };

                        Handler pdCanceller = new Handler();
                        pdCanceller.postDelayed(progressRunnable, 3000);
                    }

                    if(i==6){
                        final ProgressDialog dialog =
                                new ProgressDialog(AddHealthissue.this);
                        dialog.setIcon(R.drawable.add);
                        dialog.setTitle("Prediction Process Is Start...");
                        dialog.setMessage("Please wait...");
                        dialog.show();

                        //   long i = adapter.InsertHealthissue(medicalid,disdate, dname, sname);
                        //Toast.makeText(getApplicationContext(), "Health Issue Register Sucessfully..." + i, Toast.LENGTH_SHORT).show();

                        final Runnable progressRunnable = new Runnable() {

                            @Override
                            public void run() {
                                dialog.cancel();
                                String mpass = null;

                                final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.
                                        Builder(AddHealthissue.this);

                                alertDialogBuilder.setMessage("Suggest Doctor :- Dr.Santosh Gaikwad\n" +
                                        "Diet Plan:-The Atkins Diet.\n" +
                                        "Exercise:- Gym ..");

                                final android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialogBuilder.setPositiveButton("OK.",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface arg0, int arg1) {




                                            }
                                        });
                                android.app.AlertDialog alDialog = alertDialogBuilder.create();
                                alDialog.show();
                            }

                        };

                        Handler pdCanceller = new Handler();
                        pdCanceller.postDelayed(progressRunnable, 3000);
                    }

                    if(i==7){
                        final ProgressDialog dialog =
                                new ProgressDialog(AddHealthissue.this);
                        dialog.setIcon(R.drawable.add);
                        dialog.setTitle("Prediction Process Is Start...");
                        dialog.setMessage("Please wait...");
                        dialog.show();

                        //   long i = adapter.InsertHealthissue(medicalid,disdate, dname, sname);
                        //Toast.makeText(getApplicationContext(), "Health Issue Register Sucessfully..." + i, Toast.LENGTH_SHORT).show();

                        final Runnable progressRunnable = new Runnable() {

                            @Override
                            public void run() {
                                dialog.cancel();
                                String mpass = null;

                                final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.
                                        Builder(AddHealthissue.this);

                                alertDialogBuilder.setMessage("Suggest Doctor :- Dr.Abhishek Khalate\n" +
                                        "Diet Plan:-The HCG Diet.\n" +
                                        "Exercise:- Gym ..");

                                final android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialogBuilder.setPositiveButton("OK.",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface arg0, int arg1) {




                                            }
                                        });
                                android.app.AlertDialog alDialog = alertDialogBuilder.create();
                                alDialog.show();
                            }

                        };

                        Handler pdCanceller = new Handler();
                        pdCanceller.postDelayed(progressRunnable, 3000);
                    }


                    if(i==8){
                        final ProgressDialog dialog =
                                new ProgressDialog(AddHealthissue.this);
                        dialog.setIcon(R.drawable.add);
                        dialog.setTitle("Prediction Process Is Start...");
                        dialog.setMessage("Please wait...");
                        dialog.show();

                        //   long i = adapter.InsertHealthissue(medicalid,disdate, dname, sname);
                        //Toast.makeText(getApplicationContext(), "Health Issue Register Sucessfully..." + i, Toast.LENGTH_SHORT).show();

                        final Runnable progressRunnable = new Runnable() {

                            @Override
                            public void run() {
                                dialog.cancel();
                                String mpass = null;

                                final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.
                                        Builder(AddHealthissue.this);

                                alertDialogBuilder.setMessage("Suggest Doctor :- Dr.Amit Kadam\n" +
                                        "Diet Plan:-The Zone Diet.\n" +
                                        "Exercise:- Walk ..");

                                final android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialogBuilder.setPositiveButton("OK.",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface arg0, int arg1) {




                                            }
                                        });
                                android.app.AlertDialog alDialog = alertDialogBuilder.create();
                                alDialog.show();
                            }

                        };

                        Handler pdCanceller = new Handler();
                        pdCanceller.postDelayed(progressRunnable, 3000);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });



        }catch (Exception e){}
        }

    private void AddHelthissue() {
        final ProgressDialog dialog =
                new ProgressDialog(AddHealthissue.this);
        dialog.setIcon(R.drawable.add);
        dialog.setTitle("Processing Your Request...");
        dialog.setMessage("Please wait...");
        dialog.show();

      long i = adapter.InsertHealthissue(medicalid,disdate, dname, sname);
        //Toast.makeText(getApplicationContext(), "Health Issue Register Sucessfully..." + i, Toast.LENGTH_SHORT).show();

        final Runnable progressRunnable = new Runnable() {

            @Override
            public void run() {
                dialog.cancel();
                String mpass = null;

                final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.
                        Builder(AddHealthissue.this);

                alertDialogBuilder.setTitle("Disease Add Successfully ..");

                final android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialogBuilder.setPositiveButton("OK.",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                               /* Intent i = new Intent(AddHealthissue.this, UserHomeActivity.class);
                                i.putExtra("MedicalID",medicalid);
                                startActivity(i);*/


                            }
                        });
                android.app.AlertDialog alDialog = alertDialogBuilder.create();
                alDialog.show();
            }

        };

        Handler pdCanceller = new Handler();
        pdCanceller.postDelayed(progressRunnable, 3000);

    }

    }
