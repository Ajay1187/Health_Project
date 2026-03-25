package com.example.healthcard_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
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
                    showUserInformationDialog(id);
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

    private void showUserInformationDialog(String adharId) {
        Cursor userCursor = adapter.getalluserdetails(adharId);
        if (userCursor == null || !userCursor.moveToFirst()) {
            return;
        }

        String medicalId = userCursor.getString(0);
        String name = userCursor.getString(1);
        String address = userCursor.getString(2);
        String mobile = userCursor.getString(3);
        String dob = userCursor.getString(5);

        String currentDiseaseName = "N/A";
        String currentDiseaseSymptoms = "N/A";
        Cursor currentCursor = adapter.getLatestCurrentDisease(medicalId);
        if (currentCursor != null && currentCursor.moveToFirst()) {
            currentDiseaseName = currentCursor.getString(2);
            currentDiseaseSymptoms = currentCursor.getString(3);
        }
        if (currentCursor != null) currentCursor.close();

        String oldDiseaseHistory = "N/A";
        String recoveredDate = "N/A";
        Cursor oldCursor = adapter.getLatestOldDisease(medicalId);
        if (oldCursor != null && oldCursor.moveToFirst()) {
            oldDiseaseHistory = oldCursor.getString(2);
            recoveredDate = oldCursor.getString(1);
        }
        if (oldCursor != null) oldCursor.close();
        userCursor.close();

        AlertDialog.Builder alertbuilder = new AlertDialog.Builder(context);
        alertbuilder.setTitle("User Information");

        TableLayout layout = new TableLayout(context);
        layout.setPadding(16, 16, 16, 16);
        layout.setGravity(Gravity.START);

        addInfoRow(layout, "Medical ID:", medicalId);
        addInfoRow(layout, "Name:", name);
        addInfoRow(layout, "Address:", address);
        addInfoRow(layout, "Mobile No.:", mobile);
        addInfoRow(layout, "DOB:", dob);
        addInfoRow(layout, "Adhar Card No.:", adharId);
        addInfoRow(layout, "Current Disease Name:", currentDiseaseName);
        addInfoRow(layout, "Old Disease History:", oldDiseaseHistory);
        addInfoRow(layout, "Recovered Date:", recoveredDate);
        addInfoRow(layout, "Current Disease Symptoms:", currentDiseaseSymptoms);

        alertbuilder.setView(layout);
        alertbuilder.setPositiveButton("OK", null);
        alertbuilder.show();
    }

    private void addInfoRow(TableLayout layout, String label, String value) {
        TableRow row = new TableRow(context);
        TextView labelText = new TextView(context);
        TextView valueText = new TextView(context);

        labelText.setText(label + " ");
        labelText.setTextSize(16);
        labelText.setTypeface(null, android.graphics.Typeface.BOLD);

        valueText.setText(value == null || value.trim().isEmpty() ? "N/A" : value);
        valueText.setTextSize(16);

        row.addView(labelText);
        row.addView(valueText);
        row.setPadding(0, 10, 0, 10);
        layout.addView(row);
    }
}
