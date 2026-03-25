package com.example.healthcard_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.graphics.Color;
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
        header.setBackgroundColor(Color.parseColor("#2D6F8E"));
        addHeader("Disease", header);
        addHeader("Recovery Date", header);
        addHeader("Symptoms", header);
        table.addView(header);

        Cursor cursor = adapter.selectDisesehistory(usermedicalid);
        int rowIndex = 0;
        while (cursor.moveToNext()) {
            TableRow row = new TableRow(this);
            int rowColor = rowIndex % 2 == 0
                    ? Color.parseColor("#F4F9FC")
                    : Color.parseColor("#EAF3F8");
            row.setBackgroundColor(rowColor);

            addCell(cursor.getString(2), row); // disease
            addCell(cursor.getString(1), row); // recovery date
            addCell(cursor.getString(3), row); // symptoms
            table.addView(row);
            rowIndex++;
        }
        cursor.close();
    }

    private void addHeader(String text, TableRow row) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextSize(16);
        tv.setTypeface(tv.getTypeface(), android.graphics.Typeface.BOLD);
        tv.setTextColor(Color.WHITE);
        tv.setPadding(12, 12, 12, 12);
        row.addView(tv);
    }

    private void addCell(String text, TableRow row) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextColor(Color.parseColor("#123247"));
        tv.setPadding(12, 12, 12, 12);
        row.addView(tv);
    }
}
