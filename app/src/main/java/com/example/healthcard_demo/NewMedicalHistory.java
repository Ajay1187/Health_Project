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
        header.setBackgroundColor(getResources().getColor(R.color.purple_500));
        addHeader("Disease", header);
        addHeader("Appointment Date", header);
        addHeader("Symptoms", header);
        table.addView(header);

        Cursor cursor = adapter.selectHelathissue(usermedicalid);
        int rowIndex = 0;
        while (cursor.moveToNext()) {
            String mid = cursor.getString(0);
            String appointmentDate = cursor.getString(1);
            String disease = cursor.getString(2);
            String symptoms = cursor.getString(3);

            TableRow row = new TableRow(this);
            int rowColor = rowIndex % 2 == 0
                    ? getResources().getColor(R.color.purple_100)
                    : Color.parseColor("#E1DAF3");
            row.setBackgroundColor(rowColor);

            addCell(disease, row);
            addCell(appointmentDate, row);
            addCell(symptoms, row);
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
        tv.setTextColor(Color.BLACK);
        tv.setPadding(12, 12, 12, 12);
        row.addView(tv);
    }
}
