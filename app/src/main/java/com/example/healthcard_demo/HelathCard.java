package com.example.healthcard_demo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.print.PrintHelper;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;

public class HelathCard extends AppCompatActivity {

    private static final String DEFAULT_BLOOD_GROUP = "O+";

    private TextView txtMid;
    private TextView txtName;
    private TextView txtAddress;
    private TextView txtPhone;
    private TextView txtDob;
    private ImageView imgQr;
    private CardView smartCardContainer;

    private TestAdapter adapter;
    private String medicalid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helath_card);

        txtMid = findViewById(R.id.txt_patientid);
        txtName = findViewById(R.id.txt_pname);
        txtAddress = findViewById(R.id.txt_address);
        txtPhone = findViewById(R.id.txt_pphoneee);
        txtDob = findViewById(R.id.txt_pdatav);
        imgQr = findViewById(R.id.image_qr_dynamic);
        smartCardContainer = findViewById(R.id.smart_card_container);
        Button btnPrint = findViewById(R.id.btn_printcard);

        btnPrint.setOnClickListener(v -> printSmartCard());

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
        String age = calculateAge(dob);

        txtName.setText("Name: " + name);
        txtDob.setText("DOB: " + dob);
        txtAddress.setText("Address: " + address);
        txtPhone.setText("Mobile No: " + mobile);
        txtMid.setText("M ID: " + safe(medicalid));

        String qrPayload = buildQrPayload(name, age, dob, address, mobile, medicalid, adhar, currentDiseases, oldDiseases);
        Bitmap qrBitmap = generateQrCode(qrPayload, 900, 900);
        if (qrBitmap != null) {
            imgQr.setImageBitmap(qrBitmap);
        }
    }

    private void printSmartCard() {
        if (smartCardContainer == null || smartCardContainer.getWidth() == 0 || smartCardContainer.getHeight() == 0) {
            Toast.makeText(this, "Card not ready to print.", Toast.LENGTH_SHORT).show();
            return;
        }

        Bitmap bitmap = Bitmap.createBitmap(smartCardContainer.getWidth(), smartCardContainer.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        smartCardContainer.draw(canvas);

        PrintHelper printHelper = new PrintHelper(this);
        printHelper.setScaleMode(PrintHelper.SCALE_MODE_FIT);
        printHelper.printBitmap("Smart Health Card", bitmap);
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

    private String buildQrPayload(String name, String age, String dob, String address, String mobile,
                                  String mid, String adhar, String currentDiseases, String oldDiseases) {
        return "Name: " + name + "\n"
                + "Address: " + address + "\n"
                + "Mobile: " + mobile + "\n"
                + "M.ID: " + mid + "\n"
                + "DOB: " + dob + "\n"
                + "Adhar ID: " + adhar + "\n"
                + "Age: " + age + "\n"
                + "Blood Group: " + DEFAULT_BLOOD_GROUP + "\n"
                + "Current Disease: " + currentDiseases + "\n"
                + "Old Disease History: " + oldDiseases;
    }

    private Bitmap generateQrCode(String value, int width, int height) {
        try {
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
            hints.put(EncodeHintType.MARGIN, 2);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

            BitMatrix bitMatrix = new MultiFormatWriter().encode(value, BarcodeFormat.QR_CODE, width, height, hints);
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
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
