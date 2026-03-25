package com.example.healthcard_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class OldMedicalHistory extends AppCompatActivity {

    private TableLayout table;
    private TestAdapter adapter;
    private String usermedicalid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_old_medical_history);

        table = findViewById(R.id.table_old);

        adapter = new TestAdapter(this);
        adapter.createDatabase();
        adapter.open();

        usermedicalid = getIntent().getStringExtra("MedicalID");
        loadOldDiseases();
    }

    private void loadOldDiseases() {
        table.removeAllViews();

        TableRow header = new TableRow(this);
        addHeader("Disease", header);
        addHeader("Recovery Date", header);
        addHeader("Symptoms", header);
        table.addView(header);

        Cursor cursor = adapter.selectDisesehistory(usermedicalid);
        while (cursor.moveToNext()) {
            TableRow row = new TableRow(this);
            addCell(cursor.getString(2), row); // disease
            addCell(cursor.getString(1), row); // recovery date
            addCell(cursor.getString(3), row); // symptoms
            table.addView(row);
        }
        cursor.close();
    }

    private void addHeader(String text, TableRow row) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextSize(16);
        tv.setPadding(10, 10, 10, 10);
        row.addView(tv);
    }

    private void addCell(String text, TableRow row) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setPadding(10, 10, 10, 10);
        row.addView(tv);
    }
}
