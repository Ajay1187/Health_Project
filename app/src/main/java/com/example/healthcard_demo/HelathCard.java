package com.example.healthcard_demo;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class HelathCard extends AppCompatActivity {

    TextView pid,pname,address,phone,bdate;
    TestAdapter adapter;
    String medicalid;
    Button b1;

    int pageHeight = 1120;
    int pagewidth = 792;

    // creating a bitmap variable
    // for storing our images
    Bitmap bmp, scaledbmp;

    // constant code for runtime permissions
    private static final int PERMISSION_REQUEST_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helath_card);
        pid = (TextView) findViewById(R.id.txt_patientid);
        pname=(TextView) findViewById(R.id.txt_pname);
        address=(TextView) findViewById(R.id.txt_adddressss);
        phone=(TextView) findViewById(R.id.txt_pphoneee);
        bdate=(TextView) findViewById(R.id.txt_pdatav);
        b1=(Button) findViewById(R.id.btn_printcard);


      //  generatePDFbtn = findViewById(R.id.idBtnGeneratePDF);

        try {
            adapter = new TestAdapter(this);
            adapter.createDatabase();
            adapter.open();


            Bundle bundle = getIntent().getExtras();
            medicalid = bundle.getString("MedicalID");
            Cursor c=adapter.getUserdetails(medicalid);
            while (c.moveToNext()){
                pid.setText("M.ID:-"+c.getString(0));
                pname.setText("Name:-"+c.getString(1));
                address.setText("Address:-"+c.getString(2));
                phone.setText("Mobile:-"+c.getString(3));
                bdate.setText("D.O.B:-"+c.getString(5));

                // password.setText(c.getString(4));

            }

            b1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getApplicationContext(),"Please Connect Printer",Toast.LENGTH_SHORT).show();

                    userlogin();
                }
            });


        } catch (Exception e) {
        }


/*
        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.lp);
        scaledbmp = Bitmap.createScaledBitmap(bmp, 140, 140, false);

        // below code is used for
        // checking our permissions.
        if (checkPermission()) {
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
        } else {
            requestPermission();
        }

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generatePDF();
            }
        });
    }

    private void generatePDF() {
        PdfDocument pdfDocument = new PdfDocument();


        Paint paint = new Paint();
        Paint title = new Paint();


        PdfDocument.PageInfo mypageInfo = new PdfDocument.PageInfo.Builder(pagewidth, pageHeight, 1).create();
        PdfDocument.Page myPage = pdfDocument.startPage(mypageInfo);

        Canvas canvas = myPage.getCanvas();

        canvas.drawBitmap(scaledbmp, 56, 40, paint);


        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));


        title.setTextSize(15);


        title.setColor(ContextCompat.getColor(this, R.color.purple_200));


        canvas.drawText("A portal for IT professionals.", 209, 100, title);
        canvas.drawText("Geeks for Geeks", 209, 80, title);


        title.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        title.setColor(ContextCompat.getColor(this, R.color.purple_200));
        title.setTextSize(15);


        title.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("This is sample document which we have created.", 396, 560, title);


        pdfDocument.finishPage(myPage);


        File file = new File(Environment.getExternalStorageDirectory(), "GFG.pdf");

        try {

            pdfDocument.writeTo(new FileOutputStream(file));


            Toast.makeText(HelathCard.this, "PDF file generated successfully.", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {

            e.printStackTrace();
        }

        pdfDocument.close();
    }

    private boolean checkPermission() {
        // checking of permissions.
        int permission1 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int permission2 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        // requesting permissions if not provided.
        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {


                boolean writeStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean readStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if (writeStorage && readStorage) {
                    Toast.makeText(this, "Permission Granted..", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission Denied.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }*/
    }

    private void userlogin() {
        final ProgressDialog dialog =
                new ProgressDialog(HelathCard.this);
        dialog.setIcon(R.drawable.login);
        dialog.setTitle("Search");
        dialog.setMessage("Please wait Connecting to Printer \n" +
                "or Downloaded Health Card...");
        dialog.show();

        final Runnable progressRunnable = new Runnable() {

            @Override
            public void run() {
                dialog.cancel();
                String mpass = null;

                Toast.makeText(getApplicationContext(), "File Downloaded Sucessfully..." , Toast.LENGTH_SHORT).show();


            }
        };
        Handler pdCanceller = new Handler();
        pdCanceller.postDelayed(progressRunnable, 6000);

    }
}