package com.example.zybanking.ui.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
    private TextView tvForgot;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // --- 1. TỰ ĐỘNG ĐĂNG NHẬP (CHECK TRƯỚC KHI HIỆN GIAO DIỆN) ---
        if (checkAutoLogin()) {
            return; // Nếu đã login thì dừng code ở dưới lại
        }

        setContentView(R.layout.login);

        tilEmail = findViewById(R.id.til_email);
        tilPassword = findViewById(R.id.til_password);
        etEmail = (TextInputEditText) tilEmail.getEditText();
        etPassword = (TextInputEditText) tilPassword.getEditText();
        btnLogin = findViewById(R.id.btn_login);
        tvForgot = findViewById(R.id.tv_forgot);
        TextView tvFingerprint = findViewById(R.id.tv_fingerprint_login);

        tvFingerprint.setOnClickListener(v -> {
            SharedPreferences pref = getSharedPreferences("auth", MODE_PRIVATE);
            String token = pref.getString("access_token", null);
            String role = pref.getString("role", "");

            if (token == null) {
                Toast.makeText(this, "Bạn cần đăng nhập mật khẩu ít nhất 1 lần", Toast.LENGTH_SHORT).show();
                return;
            }

            showBiometricPrompt(role, token);
        });
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
                    if (response.body() != null) {
                        Log.d("LOGIN_BODY", new Gson().toJson(response.body()));
                    }

                    if (!response.isSuccessful() || response.body() == null) {
                        Toast.makeText(LoginActivity.this, "Login thất bại: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    LoginResponse res = response.body();
                    if (!"success".equals(res.status) || res.data == null) {
                        Toast.makeText(LoginActivity.this, res.message, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // ===== DATA =====
                    String userId = res.data.user.USER_ID;
                    String accessToken = res.data.access_token;
                    String role = res.data.user.ROLE;
                    String accountId = res.data.user.ACCOUNT_ID;

                    // ===== 2. SỬA LỖI LƯU SESSION =====
                    SharedPreferences pref = getSharedPreferences("auth", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit(); // Tạo editor
                    editor.putString("access_token", accessToken);
                    editor.putString("user_id", userId);
                    editor.putString("role", role);
                    if (accountId != null) {
                        editor.putString("account_id", accountId);
                        Log.d("LOGIN", "Saved Account ID: " + accountId);
                    } else {
                        Log.e("LOGIN", "Account ID is NULL!");
                    }

                    editor.apply(); // Lưu 1 lần duy nhất ở cuối

                    Log.d("LOGIN_ROLE", "ROLE = " + role);

                    // ===== ĐIỀU HƯỚNG =====
                    navigateUser(role, accessToken);
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    Toast.makeText(LoginActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
    private void showBiometricPrompt(String role, String token) {
        Executor executor = ContextCompat.getMainExecutor(this);

        BiometricPrompt biometricPrompt = new BiometricPrompt(
                this,
                executor,
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationSucceeded(
                            BiometricPrompt.AuthenticationResult result) {
                        runOnUiThread(() -> {
                            navigateUser(role, token);
                        });
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        Toast.makeText(LoginActivity.this,
                                "Xác thực thất bại", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        BiometricPrompt.PromptInfo promptInfo =
                new BiometricPrompt.PromptInfo.Builder()
                        .setTitle("Đăng nhập bằng vân tay")
                        .setSubtitle("Xác thực để tiếp tục")
                        .setNegativeButtonText("Huỷ")
                        .build();

        biometricPrompt.authenticate(promptInfo);
    }
    // Hàm kiểm tra tự động đăng nhập
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

    // Hàm điều hướng chung
    private void navigateUser(String role, String token) {
        Intent intent;
        if ("admin".equalsIgnoreCase(role)) {
            intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
        } else {
            intent = new Intent(LoginActivity.this, HomeActivity.class);
        }

        // --- QUAN TRỌNG: Truyền Token sang HomeActivity ---
        // Giúp HomeActivity có Token dùng ngay lập tức, sửa lỗi "lúc được lúc không"
        intent.putExtra("EXTRA_TOKEN", token);

        startActivity(intent);
        finish(); // Đóng LoginActivity
    }
}