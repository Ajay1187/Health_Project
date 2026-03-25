package com.example.healthcard_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class ViewUserProfile extends AppCompatActivity {

    private TestAdapter adapter;
    private EditText mid;
    private EditText name;
    private EditText address;
    private EditText phone;
    private EditText adhar;
    private EditText weight;
    private EditText bloodGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user_profile);

        mid = findViewById(R.id.txt_pmid);
        name = findViewById(R.id.txt_pname);
        address = findViewById(R.id.txt_paddress);
        phone = findViewById(R.id.txt_pphone);
        adhar = findViewById(R.id.txt_padhar);
        weight = findViewById(R.id.txt_pweight);
        bloodGroup = findViewById(R.id.txt_pbloodgroup);
        Button back = findViewById(R.id.btn_backk);

        try {
            adapter = new TestAdapter(this);
            adapter.createDatabase();
            adapter.open();

            Bundle bundle = getIntent().getExtras();
            String medicalid = bundle == null ? "" : bundle.getString("MedicalID", "");

            Cursor c = adapter.getUserdetails(medicalid);
            if (c.moveToFirst()) {
                mid.setText(c.getString(0));
                name.setText(c.getString(1));
                address.setText(c.getString(2));
                phone.setText(c.getString(3));
                adhar.setText(c.getString(4));
                weight.setText("70 kg");
                bloodGroup.setText("O+");
            }
            c.close();
        } catch (Exception ignored) {
        }

        back.setOnClickListener(view -> finish());
    }
}
