package com.example.zybanking.ui.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.zybanking.R;
import com.example.zybanking.ui.dashboard.AdminDashboardActivity;
import com.example.zybanking.ui.dashboard.HomeActivity;
import com.example.zybanking.data.remote.ApiService;
import com.example.zybanking.data.remote.RetrofitClient;
import com.example.zybanking.data.models.auth.LoginRequest;
import com.example.zybanking.data.models.auth.LoginResponse;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout tilEmail, tilPassword;
    private TextInputEditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvForgot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        tilEmail = findViewById(R.id.til_email);
        tilPassword = findViewById(R.id.til_password);
        etEmail = (TextInputEditText) tilEmail.getEditText();
        etPassword = (TextInputEditText) tilPassword.getEditText();
        btnLogin = findViewById(R.id.btn_login);
        tvForgot = findViewById(R.id.tv_forgot);

        tvForgot.setOnClickListener(v -> {
            startActivity(new Intent(this, ForgotPasswordActivity.class));
        });

        btnLogin.setOnClickListener(v -> {

            String email = etEmail != null ? etEmail.getText().toString().trim() : "";
            String password = etPassword != null ? etPassword.getText().toString().trim() : "";

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập email và mật khẩu", Toast.LENGTH_SHORT).show();
                return;
            }

            LoginRequest request = new LoginRequest(email, password);
            ApiService api = RetrofitClient.getClient().create(ApiService.class);

            api.login(request).enqueue(new Callback<LoginResponse>() {

                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {

                    Log.d("LOGIN_HTTP", "HTTP = " + response.code());

                    if (response.body() != null) {
                        Log.d("LOGIN_BODY", new Gson().toJson(response.body()));
                    }

                    if (!response.isSuccessful() || response.body() == null) {
                        Toast.makeText(
                                LoginActivity.this,
                                "Login thất bại (HTTP " + response.code() + ")",
                                Toast.LENGTH_SHORT
                        ).show();
                        return;
                    }

                    LoginResponse res = response.body();

                    if (!"success".equals(res.status) || res.data == null) {
                        Toast.makeText(
                                LoginActivity.this,
                                res.message != null ? res.message : "Login failed",
                                Toast.LENGTH_SHORT
                        ).show();
                        return;
                    }

                    // ===== DATA =====
                    String userId = res.data.user.USER_ID;
                    String accessToken = res.data.access_token;
                    String role = res.data.user.ROLE;

                    // ===== LƯU SESSION (CHỈ 1 LẦN) =====
                    SharedPreferences pref = getSharedPreferences("auth", MODE_PRIVATE);
                    pref.edit()
                            .putString("access_token", accessToken)
                            .putString("user_id", userId)
                            .putString("role", role)
                            .apply();

                    Log.d("LOGIN_ROLE", "ROLE = " + role);

                    // ===== ĐIỀU HƯỚNG =====
                    Intent intent;
                    if ("admin".equalsIgnoreCase(role)) {
                        intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
                    } else {
                        intent = new Intent(LoginActivity.this, HomeActivity.class);
                    }

                    startActivity(intent);
                    finish();
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                    t.printStackTrace();
                }
            });
        });
    }
}
