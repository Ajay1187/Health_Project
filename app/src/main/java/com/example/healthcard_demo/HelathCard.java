package com.example.healthcard_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class HelathCard extends AppCompatActivity {

    private TextView txtMid;
    private TextView txtName;
    private TextView txtAddress;
    private TextView txtPhone;
    private TextView txtDob;
    private TextView txtAdhar;
    private TextView txtAge;
    private TextView txtBloodGroup;
    private TextView txtCurrentDisease;
    private TextView txtOldDisease;
    private ImageView imgQr;

    private TestAdapter adapter;
    private String medicalid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helath_card);

        txtMid = findViewById(R.id.txt_patientid);
        txtName = findViewById(R.id.txt_pname);
        txtAddress = findViewById(R.id.txt_adddressss);
        txtPhone = findViewById(R.id.txt_pphoneee);
        txtDob = findViewById(R.id.txt_pdatav);
        txtAdhar = findViewById(R.id.txt_adhar);
        txtAge = findViewById(R.id.txt_age);
        txtBloodGroup = findViewById(R.id.txt_blood_group);
        txtCurrentDisease = findViewById(R.id.txt_current_disease);
        txtOldDisease = findViewById(R.id.txt_old_disease);
        imgQr = findViewById(R.id.image_qr_dynamic);
        Button btnBack = findViewById(R.id.btn_printcard);

        btnBack.setOnClickListener(v -> finish());

        try {
            adapter = new TestAdapter(this);
            adapter.createDatabase();
            adapter.open();

            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                medicalid = bundle.getString("MedicalID", "");
            }
            loadCardData();
        } catch (Exception exception) {
            Toast.makeText(this, "Unable to load health card.", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadCardData() {
        String name = "";
        String address = "";
        String mobile = "";
        String adhar = "";
        String dob = "";

        Cursor c = adapter.getUserdetails(medicalid);
        if (c.moveToFirst()) {
            name = safe(c.getString(1));
            address = safe(c.getString(2));
            mobile = safe(c.getString(3));
            adhar = safe(c.getString(4));
            dob = safe(c.getString(5));
        }
        c.close();

        String currentDiseases = loadDiseaseNames(adapter.selectHelathissue(medicalid), 2);
        String oldDiseases = loadDiseaseNames(adapter.selectDisesehistory(medicalid), 2);

        txtMid.setText("M.ID: " + safe(medicalid));
        txtName.setText("Full Name: " + name);
        txtAddress.setText("Address: " + address);
        txtPhone.setText("Mobile: " + mobile);
        txtDob.setText("D.O.B: " + dob);
        txtAdhar.setText("Adhar: " + adhar);
        txtAge.setText("Age: " + calculateAge(dob));
        txtBloodGroup.setText("Blood Group: O+");
        txtCurrentDisease.setText("Current Disease(s): " + currentDiseases);
        txtOldDisease.setText("Old Disease(s): " + oldDiseases);

        String qrPayload = buildQrPayload(name, dob, address, mobile, medicalid, adhar, currentDiseases, oldDiseases);
        Bitmap qrBitmap = generateQrCode(qrPayload, 420, 420);
        if (qrBitmap != null) {
            imgQr.setImageBitmap(qrBitmap);
        }
    }

    private String loadDiseaseNames(Cursor cursor, int diseaseColumnIndex) {
        List<String> names = new ArrayList<>();
        while (cursor.moveToNext()) {
            String disease = safe(cursor.getString(diseaseColumnIndex));
            if (!TextUtils.isEmpty(disease) && !names.contains(disease)) {
                names.add(disease);
            }
        }
        cursor.close();
        return names.isEmpty() ? "None" : TextUtils.join(", ", names);
    }

    private String buildQrPayload(String name, String dob, String address, String mobile,
                                  String mid, String adhar, String currentDiseases, String oldDiseases) {
        return "Name: " + name + "\n"
                + "Age: " + calculateAge(dob) + "\n"
                + "DOB: " + dob + "\n"
                + "Blood Group: O+\n"
                + "Address: " + address + "\n"
                + "Mobile: " + mobile + "\n"
                + "Medical ID: " + mid + "\n"
                + "Adhar ID: " + adhar + "\n"
                + "Current Disease(s): " + currentDiseases + "\n"
                + "Old Disease(s): " + oldDiseases;
    }

    private Bitmap generateQrCode(String value, int width, int height) {
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(value, BarcodeFormat.QR_CODE, width, height);
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            return bitmap;
        } catch (WriterException e) {
            return null;
        }
    }

    private String calculateAge(String dobValue) {
        try {
            String[] parts = dobValue.split("/");
            if (parts.length == 3) {
                int year = Integer.parseInt(parts[2]);
                int currentYear = Calendar.getInstance(Locale.getDefault()).get(Calendar.YEAR);
                int age = Math.max(0, currentYear - year);
                return String.valueOf(age);
            }
        } catch (Exception ignored) {
        }
        return "N/A";
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }
}
