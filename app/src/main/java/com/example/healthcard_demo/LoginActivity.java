package com.example.healthcard_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private TextView tvRegister, tvForgetPassword;
    private Button btnLogin;
    private EditText edtMedicalId, edtPassword;
    private ProgressBar progressBar;

    private TestAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tvRegister = findViewById(R.id.register);
        tvForgetPassword = findViewById(R.id.password_link);
        btnLogin = findViewById(R.id.btn_login);
        edtMedicalId = findViewById(R.id.txt_mobile);
        edtPassword = findViewById(R.id.txt_password);
        progressBar = findViewById(R.id.progressBar);

        progressBar.setVisibility(View.GONE);

        try {
            adapter = new TestAdapter(this);
            adapter.createDatabase();
            adapter.open();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this,
                    "Database error: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }

        tvForgetPassword.setOnClickListener(view -> {
            Intent i = new Intent(LoginActivity.this, ForgetPassword.class);
            startActivity(i);
        });

        tvRegister.setOnClickListener(view -> {
            Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(i);
        });

        btnLogin.setOnClickListener(view -> {
            String medicalId = edtMedicalId.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (TextUtils.isEmpty(medicalId)) {
                edtMedicalId.setError("Medical Id is Required.");
                return;
            }

            if (TextUtils.isEmpty(password)) {
                edtPassword.setError("Password is Required.");
                return;
            }

            if (adapter == null) {
                Toast.makeText(this, "Database is not ready. Please restart app.", Toast.LENGTH_SHORT).show();
                return;
            }

            int result = adapter.checkUserLogin(medicalId, password);

            if (result == 1) {
                userLogin(medicalId);
                return;
            }

            if (medicalId.equalsIgnoreCase("9197")
                    && password.equalsIgnoreCase("1234")) {

                Intent intent = new Intent(
                        getApplicationContext(),
                        DoctorHomeActivity.class);

                startActivity(intent);

                Toast.makeText(LoginActivity.this,
                        "Login Successful",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(LoginActivity.this,
                    "Invalid Medical ID Or Password",
                    Toast.LENGTH_SHORT).show();
        });
    }

    private void userLogin(String medicalId) {

        progressBar.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            progressBar.setVisibility(View.GONE);
            btnLogin.setEnabled(true);

            Intent i = new Intent(
                    LoginActivity.this,
                    UserHomeActivity.class);

            i.putExtra("Key", medicalId);
            startActivity(i);

            Toast.makeText(getApplicationContext(),
                    "Your Login is Successful...",
                    Toast.LENGTH_SHORT).show();

            edtMedicalId.setText("");
            edtPassword.setText("");

        }, 2000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adapter != null) {
            adapter.close();
        }
    }
}
