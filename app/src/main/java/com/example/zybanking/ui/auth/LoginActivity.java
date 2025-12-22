package com.example.zybanking.ui.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

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

import java.util.concurrent.Executor;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout tilEmail, tilPassword;
    private TextInputEditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvForgot, tvFaceID;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // --- 1. AUTO LOGIN ---
        if (checkAutoLogin()) return;

        setContentView(R.layout.login);

        apiService = RetrofitClient.getClient().create(ApiService.class);

        tilEmail = findViewById(R.id.til_email);
        tilPassword = findViewById(R.id.til_password);
        etEmail = (TextInputEditText) tilEmail.getEditText();
        etPassword = (TextInputEditText) tilPassword.getEditText();
        btnLogin = findViewById(R.id.btn_login);
        tvForgot = findViewById(R.id.tv_forgot);
        tvFaceID = findViewById(R.id.tv_faceid_login);

        // ===== QUÊN MẬT KHẨU =====
        tvForgot.setOnClickListener(v ->
                startActivity(new Intent(this, ForgotPasswordActivity.class))
        );

        // ===== LOGIN BẰNG EMAIL / PASSWORD =====
        btnLogin.setOnClickListener(v -> {
            String email = etEmail != null ? etEmail.getText().toString().trim() : "";
            String password = etPassword != null ? etPassword.getText().toString().trim() : "";

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập email và mật khẩu", Toast.LENGTH_SHORT).show();
                return;
            }

            apiService.login(new LoginRequest(email, password))
                    .enqueue(new Callback<LoginResponse>() {
                        @Override
                        public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                            if (!response.isSuccessful() || response.body() == null) {
                                Toast.makeText(LoginActivity.this,
                                        "Login thất bại: " + response.code(),
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }

                            LoginResponse res = response.body();
                            if (!"success".equals(res.status) || res.data == null) {
                                Toast.makeText(LoginActivity.this,
                                        res.message,
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }

                            saveSession(res);
                            navigateUser(res.data.user.ROLE, res.data.access_token);
                        }

                        @Override
                        public void onFailure(Call<LoginResponse> call, Throwable t) {
                            Toast.makeText(LoginActivity.this,
                                    "Lỗi mạng: " + t.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // ===== BIOMETRIC LOGIN (DEMO b@gmail.com) =====
        tvFaceID.setOnClickListener(v -> {
            SharedPreferences pref = getSharedPreferences("auth", MODE_PRIVATE);
            String token = pref.getString("access_token", null);
            String role = pref.getString("role", null);

            if (token == null || role == null) {
                // demo: lấy token theo email cố định
                fetchTokenByEmail("b@gmail.com");
                return;
            }

            showBiometricPrompt(role, token);
        });
    }

    // ================= AUTO LOGIN =================

    private boolean checkAutoLogin() {
        SharedPreferences pref = getSharedPreferences("auth", MODE_PRIVATE);
        String token = pref.getString("access_token", null);
        String role = pref.getString("role", "");

        if (token != null && !token.isEmpty()) {
            navigateUser(role, token);
            return true;
        }
        return false;
    }

    // ================= BIOMETRIC =================

    private void showBiometricPrompt(String role, String token) {
        Executor executor = ContextCompat.getMainExecutor(this);

        BiometricPrompt biometricPrompt =
                new BiometricPrompt(this, executor,
                        new BiometricPrompt.AuthenticationCallback() {

                            @Override
                            public void onAuthenticationSucceeded(
                                    @NonNull BiometricPrompt.AuthenticationResult result) {
                                Toast.makeText(LoginActivity.this,
                                        "Xác thực thành công",
                                        Toast.LENGTH_SHORT).show();
                                navigateUser(role, token);
                            }

                            @Override
                            public void onAuthenticationError(int errorCode,
                                                              @NonNull CharSequence errString) {
                                // demo cho giảng viên: máy không hỗ trợ vẫn cho vào
                                if (errorCode == 11) {
                                    navigateUser(role, token);
                                } else {
                                    Toast.makeText(LoginActivity.this,
                                            errString,
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

        BiometricPrompt.PromptInfo promptInfo =
                new BiometricPrompt.PromptInfo.Builder()
                        .setTitle("Đăng nhập nhanh")
                        .setSubtitle("Xác thực sinh trắc học")
                        .setNegativeButtonText("Hủy")
                        .setAllowedAuthenticators(
                                androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
                                        | androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
                        )
                        .build();

        biometricPrompt.authenticate(promptInfo);
    }

    // ================= FETCH TOKEN DEMO =================

    private void fetchTokenByEmail(String email) {
        Toast.makeText(this,
                "Khôi phục phiên đăng nhập...",
                Toast.LENGTH_SHORT).show();

        apiService.getLastToken(email)
                .enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call,
                                           Response<LoginResponse> response) {
                        if (!response.isSuccessful() || response.body() == null) {
                            Toast.makeText(LoginActivity.this,
                                    "Không tìm thấy phiên đăng nhập",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        LoginResponse res = response.body();
                        saveSession(res);
                        showBiometricPrompt(
                                res.data.user.ROLE,
                                res.data.access_token
                        );
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        Toast.makeText(LoginActivity.this,
                                "Lỗi server",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // ================= SESSION =================

    private void saveSession(LoginResponse res) {
        SharedPreferences.Editor editor =
                getSharedPreferences("auth", MODE_PRIVATE).edit();

        editor.putString("access_token", res.data.access_token);
        editor.putString("user_id", res.data.user.USER_ID);
        editor.putString("role", res.data.user.ROLE);
        editor.apply();
    }

    // ================= NAVIGATION =================

    private void navigateUser(String role, String token) {
        Intent intent;
        if ("admin".equalsIgnoreCase(role)) {
            intent = new Intent(this, AdminDashboardActivity.class);
        } else {
            intent = new Intent(this, HomeActivity.class);
        }

        intent.putExtra("EXTRA_TOKEN", token);
        startActivity(intent);
        finish();
    }
}
