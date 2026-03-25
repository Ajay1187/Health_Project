package com.example.healthcard_demo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NewMedicalHistory extends AppCompatActivity {

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
        setContentView(R.layout.activity_new_medical_history);

        table = findViewById(R.id.table_current);

        adapter = new TestAdapter(this);
        adapter.createDatabase();
        adapter.open();

        usermedicalid = getIntent().getStringExtra("MedicalID");

        loadCurrentDiseases();
    }

    private void loadCurrentDiseases() {
        table.removeAllViews();

        TableRow header = new TableRow(this);
        header.setBackgroundColor(Color.parseColor(HEADER_BG));
        addHeaderCell(header, "Disease", 1f);
        addHeaderCell(header, "Appointment Date", 1.2f);
        addHeaderCell(header, "Symptoms", 1.8f);
        table.addView(header);

        Cursor cursor = adapter.selectHelathissue(usermedicalid);
        int rowIndex = 0;
        while (cursor.moveToNext()) {
            String mid = cursor.getString(0);
            String appointmentDate = cursor.getString(1);
            String disease = cursor.getString(2);
            String symptoms = cursor.getString(3);

            TableRow row = new TableRow(this);
            row.setBackgroundColor(Color.parseColor(rowIndex % 2 == 0 ? ROW_BG_ONE : ROW_BG_TWO));
            addBodyCell(row, disease, 1f);
            addBodyCell(row, appointmentDate, 1.2f);
            addBodyCell(row, symptoms, 1.8f);
            table.addView(row);

            row.setOnClickListener(v -> showRecoverDialog(mid, appointmentDate, disease, symptoms));
            rowIndex++;
        }
        cursor.close();
    }

    private void showRecoverDialog(String mid, String appointmentDate, String disease, String symptoms) {
        String recoveryDate = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(new Date());
        new AlertDialog.Builder(this)
                .setTitle("Mark as Recovered?")
                .setMessage("Disease: " + disease + "\nRecovery Date: " + recoveryDate)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Recovered", (dialog, which) -> markRecovered(mid, appointmentDate, disease, symptoms, recoveryDate))
                .show();
    }

    private void markRecovered(String mid, String appointmentDate, String disease, String symptoms, String recoveryDate) {
        long inserted = adapter.InsertDisesinhistory(mid, recoveryDate, disease, symptoms);
        if (inserted > 0) {
            adapter.deleteHealthIssue(mid, appointmentDate, disease, symptoms);
            Toast.makeText(this, "Moved to old disease history.", Toast.LENGTH_SHORT).show();
            loadCurrentDiseases();
        } else {
            Toast.makeText(this, "Unable to update history.", Toast.LENGTH_SHORT).show();
        }
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
