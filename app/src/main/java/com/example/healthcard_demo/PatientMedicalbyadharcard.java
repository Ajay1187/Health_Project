package com.example.healthcard_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class PatientMedicalbyadharcard extends AppCompatActivity {
    ListView sp1;
    ArrayAdapter<String> ad;
    List<String > list;
    final Context context=this;
    EditText inputSearch;
    TestAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_medicalbyadharcard);
        sp1=(ListView) findViewById(R.id.txt_bypatientmedicalid);
        inputSearch=(EditText) findViewById(R.id.byinputSearch);

        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                PatientMedicalbyadharcard.this.ad.getFilter().filter(cs);
            }
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }
            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });

        try {
            adapter = new TestAdapter(this);
            adapter.createDatabase();
            adapter.open();

            AddMedicalId();


            sp1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    String id=sp1.getItemAtPosition(position).toString();
                  /*  Intent i = new Intent(PatientMedicalbyadharcard.this, ViewUserdetails.class);
                    i.putExtra("Key",id);
                    startActivity(i);*/

                    AlertDialog.Builder alertbuilder=new AlertDialog.Builder(context);
                    alertbuilder.setTitle("         User Information");

                    TableLayout layout=new TableLayout(context);
                    layout.setGravity(Gravity.CENTER);

                    Cursor c=adapter.getalluserdetails(id);
                    while (c.moveToNext()) {

                        final TableRow rrr=new TableRow(context);
                        TextView tvrr=new TextView(context);
                        tvrr.setText("     Medical Id");
                        tvrr.setTextSize(20);
                        tvrr.setTextColor(Color.BLACK);
                        //r.setBackgroundResource(R.drawable.row);
                        rrr.addView(tvrr);


                        //  final TableRow r=new TableRow(context);

                        final TextView tv1=new TextView(context);
                        tv1.setText("       " +c.getString(0));
                        //final String num=("     " +c.getString(0));
                        tv1.setTextSize(20);
                        tv1.setPadding(0,20,0,0);
                        tv1.setTextColor(Color.BLACK);
                        rrr.addView(tv1);
                        layout.addView(rrr);



                        final TableRow rrrt=new TableRow(context);
                        TextView tvrrt=new TextView(context);
                        tvrrt.setText("     Name");
                        tvrrt.setTextSize(20);
                        tvrrt.setTextColor(Color.BLACK);
                        //r.setBackgroundResource(R.drawable.row);
                        rrrt.addView(tvrrt);


                        //final TableRow rt=new TableRow(context);

                        final TextView tv1t=new TextView(context);
                        tv1t.setText("       " +c.getString(1));
                        //final String num=("     " +c.getString(0));
                        tv1t.setTextSize(20);
                        tv1t.setPadding(0,20,0,0);
                        tv1t.setTextColor(Color.BLACK);
                        rrrt.addView(tv1t);
                        layout.addView(rrrt);



                        final TableRow rrrta=new TableRow(context);
                        TextView tvrrta=new TextView(context);
                        tvrrta.setText("     Address");
                        tvrrta.setTextSize(20);
                        tvrrta.setTextColor(Color.BLACK);
                        //r.setBackgroundResource(R.drawable.row);
                        rrrta.addView(tvrrta);


                        // final TableRow rta=new TableRow(context);

                        final TextView tv1ta=new TextView(context);
                        tv1ta.setText("       " +c.getString(2));
                        //final String num=("     " +c.getString(0));
                        tv1ta.setTextSize(20);
                        tv1ta.setPadding(0,20,0,0);
                        tv1ta.setTextColor(Color.BLACK);
                        rrrta.addView(tv1ta);
                        layout.addView(rrrta);


                        final TableRow rrrtaa=new TableRow(context);
                        TextView tvrrtaa=new TextView(context);
                        tvrrtaa.setText("     Mobile");
                        tvrrtaa.setTextSize(20);
                        tvrrtaa.setTextColor(Color.BLACK);
                        //r.setBackgroundResource(R.drawable.row);
                        rrrtaa.addView(tvrrtaa);


                        // final TableRow rta=new TableRow(context);

                        final TextView tv1taa=new TextView(context);
                        tv1taa.setText("       " +c.getString(3));
                        //final String num=("     " +c.getString(0));
                        tv1taa.setTextSize(20);
                        tv1taa.setPadding(0,20,0,0);
                        tv1taa.setTextColor(Color.BLACK);
                        rrrtaa.addView(tv1taa);
                        layout.addView(rrrtaa);



                        final TableRow rrrtat=new TableRow(context);
                        TextView tvrrtat=new TextView(context);
                        tvrrtat.setText("     B.Date");
                        tvrrtat.setTextSize(20);
                        tvrrtat.setTextColor(Color.BLACK);
                        //r.setBackgroundResource(R.drawable.row);
                        rrrtat.addView(tvrrtat);


                        // final TableRow rta=new TableRow(context);

                        final TextView tv1tam=new TextView(context);
                        tv1tam.setText("       " +c.getString(5));
                        //final String num=("     " +c.getString(0));
                        tv1tam.setTextSize(20);
                        tv1tam.setPadding(0,20,0,0);
                        tv1tam.setTextColor(Color.BLACK);
                        rrrtat.addView(tv1tam);
                        layout.addView(rrrtat);

                        alertbuilder.setPositiveButton("OK",new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub

                            }
                        });



                        alertbuilder.setView(layout);

                    }


                    AlertDialog alertDialog= alertbuilder.create();
                    alertDialog.show();
                }
            });


        }catch (Exception e){}




    }

    private void AddMedicalId() {
        Cursor c=adapter.selectUser();
        list=new ArrayList<String>();
        while(c.moveToNext())
        {

            list.add(c.getString(4).toString());

        }
        ad=new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,list);

        sp1.setAdapter(ad);




    }
}