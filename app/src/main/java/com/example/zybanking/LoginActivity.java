package com.example.zybanking;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout tilPhone, tilPassword;
    private TextInputEditText etPhone, etPassword;
    private Button btnLogin;
    private TextView tvForgot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        tilPhone = findViewById(R.id.til_phone);
        tilPassword = findViewById(R.id.til_password);
        etPhone = (TextInputEditText) tilPhone.getEditText();       // Cách chuẩn
        etPassword = (TextInputEditText) tilPassword.getEditText(); // Cách chuẩn
        btnLogin = findViewById(R.id.btn_login);
        tvForgot = findViewById(R.id.tv_forgot);

        btnLogin.setOnClickListener(v -> {
            String phone = etPhone != null ? etPhone.getText().toString().trim() : "";
            String password = etPassword != null ? etPassword.getText().toString().trim() : "";

            if(phone.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Vui lòng nhập số điện thoại và mật khẩu", Toast.LENGTH_SHORT).show();
            } else {
                String fakeName = "User" + phone.substring(Math.max(0, phone.length() - 4));
                Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                intent.putExtra("user_name", fakeName);
                startActivity(intent);
                finish();
            }
        });

        tvForgot.setOnClickListener(v -> {
            Toast.makeText(LoginActivity.this, "Chức năng quên mật khẩu chưa được triển khai", Toast.LENGTH_SHORT).show();
        });
    }
}
