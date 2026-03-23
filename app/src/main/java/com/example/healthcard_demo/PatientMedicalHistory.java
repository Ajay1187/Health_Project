package com.example.healthcard_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class PatientMedicalHistory extends AppCompatActivity {
ListView sp1;
    ArrayAdapter<String> ad;
    List<String > list;
    final Context context=this;
    EditText inputSearch;
TestAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_medical_history);

        sp1=(ListView) findViewById(R.id.txt_patientmedicalid);
        inputSearch=(EditText) findViewById(R.id.inputSearch);

        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                PatientMedicalHistory.this.ad.getFilter().filter(cs);
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
                    Intent i = new Intent(PatientMedicalHistory.this, ViewMedicalHistory.class);
                    i.putExtra("Key",id);
                    startActivity(i);
                }
            });


        }catch (Exception e){}




    }

    private void AddMedicalId() {
        Cursor c=adapter.selectUser();
        list=new ArrayList<String>();
        while(c.moveToNext())
        {

            list.add(c.getString(0).toString());

        }
        ad=new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,list);

        sp1.setAdapter(ad);
    }
}