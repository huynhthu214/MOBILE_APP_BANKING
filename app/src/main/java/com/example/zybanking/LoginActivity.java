package com.example.zybanking;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import com.google.gson.Gson;

import com.example.zybanking.data.remote.ApiService;
import com.example.zybanking.data.remote.RetrofitClient;
import com.example.zybanking.data.models.LoginRequest;
import com.example.zybanking.data.models.LoginResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

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

                    // ===== LOG HTTP CODE =====
                    Log.d("LOGIN_HTTP", "HTTP CODE = " + response.code());

                    // ===== LOG BODY (SUCCESS) =====
                    if (response.body() != null) {
                        Log.d("LOGIN_BODY", new Gson().toJson(response.body()));
                    }

                    // ===== LOG BODY (ERROR) =====
                    if (response.errorBody() != null) {
                        try {
                            Log.e("LOGIN_ERROR", response.errorBody().string());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    // ===== XỬ LÝ LOGIC =====
                    if (response.isSuccessful() && response.body() != null) {

                        LoginResponse res = response.body();

                        if ("success".equals(res.status) && res.data != null) {

                            String userId = res.data.user.USER_ID;
                            String accessToken = res.data.access_token;

                            // ===== LƯU TOKEN =====
                            SharedPreferences pref =
                                    getSharedPreferences("auth", MODE_PRIVATE);

                            pref.edit()
                                    .putString("access_token", accessToken)
                                    .putString("user_id", userId)
                                    .apply();

                            // ===== CHUYỂN MÀN =====
                            startActivity(
                                    new Intent(LoginActivity.this, DashboardActivity.class)
                            );
                            finish();

                        } else {
                            Toast.makeText(
                                    LoginActivity.this,
                                    res.message != null ? res.message : "Login failed",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }

                    } else {
                        Toast.makeText(
                                LoginActivity.this,
                                "Login thất bại (HTTP " + response.code() + ")",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }


                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    Toast.makeText(LoginActivity.this,
                            t.getMessage(),
                            Toast.LENGTH_LONG).show();
                    t.printStackTrace();
                }
            });
        });
    }
}
