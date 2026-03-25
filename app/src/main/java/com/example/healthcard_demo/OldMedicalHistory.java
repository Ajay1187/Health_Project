package com.example.healthcard_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class OldMedicalHistory extends AppCompatActivity {

    private static final String HEADER_BG = "#DDEFFD";
    private static final String ROW_BG_ONE = "#FFFFFF";
    private static final String ROW_BG_TWO = "#F5FAFF";
    private static final String HEADER_TEXT = "#1E3A5F";
    private static final String BODY_TEXT = "#0F2027";

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
        header.setBackgroundColor(Color.parseColor(HEADER_BG));
        addHeaderCell(header, "Disease", 1f);
        addHeaderCell(header, "Recovery Date", 1.2f);
        addHeaderCell(header, "Symptoms", 1.8f);
        table.addView(header);

        Cursor cursor = adapter.selectDisesehistory(usermedicalid);
        int rowIndex = 0;
        while (cursor.moveToNext()) {
            TableRow row = new TableRow(this);
            row.setBackgroundColor(Color.parseColor(rowIndex % 2 == 0 ? ROW_BG_ONE : ROW_BG_TWO));

            addBodyCell(row, cursor.getString(2), 1f);
            addBodyCell(row, cursor.getString(1), 1.2f);
            addBodyCell(row, cursor.getString(3), 1.8f);
            table.addView(row);
            rowIndex++;
        }
        cursor.close();
    }

    private void addHeaderCell(TableRow row, String text, float weight) {
        TextView tv = buildCell(text, weight);
        tv.setTextColor(Color.parseColor(HEADER_TEXT));
        tv.setTypeface(tv.getTypeface(), android.graphics.Typeface.BOLD);
        row.addView(tv);
    }

    private void addBodyCell(TableRow row, String text, float weight) {
        TextView tv = buildCell(text, weight);
        tv.setTextColor(Color.parseColor(BODY_TEXT));
        row.addView(tv);
    }

    private TextView buildCell(String text, float weight) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextSize(16);
        tv.setPadding(12, 12, 12, 12);
        tv.setSingleLine(false);
        tv.setMaxLines(4);
        tv.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, weight));
        return tv;
    }
}
