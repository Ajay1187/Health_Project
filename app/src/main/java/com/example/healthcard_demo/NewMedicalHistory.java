package com.example.healthcard_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class NewMedicalHistory extends AppCompatActivity {

    TableLayout table;
    TestAdapter adapter;
    String usermedicalid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_medical_history);

        table = findViewById(R.id.table_current);

        adapter = new TestAdapter(this);
        adapter.createDatabase();
        adapter.open();

        usermedicalid = getIntent().getStringExtra("MedicalID");

        loadCurrentDiseases();
    }

    private void loadCurrentDiseases() {

        TableRow header = new TableRow(this);

        addHeader("Disease", header);
        addHeader("Appointment Date", header);
        addHeader("Symptoms", header);
        addHeader("Severity", header);

        table.addView(header);

        Cursor cursor = adapter.selectDisesehistory(usermedicalid);

        while (cursor.moveToNext()) {

            // show only NOT recovered
            if (!cursor.getString(1).equalsIgnoreCase("Yes")) {

                TableRow row = new TableRow(this);

                addCell(cursor.getString(2), row); // disease
                addCell(cursor.getString(3), row); // appointment date
                addCell(cursor.getString(4), row); // symptoms
                addCell(cursor.getString(5), row); // severity

                table.addView(row);
            }
        }

        cursor.close();
    }

    private void addHeader(String text, TableRow row) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextSize(16);
        row.addView(tv);
    }

    private void addCell(String text, TableRow row) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setPadding(10,10,10,10);
        row.addView(tv);
    }
}