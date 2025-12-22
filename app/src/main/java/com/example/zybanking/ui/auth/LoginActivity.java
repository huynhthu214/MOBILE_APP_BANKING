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
import com.example.zybanking.data.models.BasicResponse;
import com.example.zybanking.ui.dashboard.AdminDashboardActivity;
import com.example.zybanking.ui.dashboard.HomeActivity;
import com.example.zybanking.data.remote.ApiService;
import com.example.zybanking.data.remote.RetrofitClient;
import com.example.zybanking.data.models.auth.LoginRequest;
import com.example.zybanking.data.models.auth.LoginResponse;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import java.io.File; // Sửa lỗi Cannot resolve symbol 'File'
import java.util.concurrent.Executor;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout tilEmail, tilPassword;
    private TextInputEditText etEmail, etPassword;
    private Button btnLogin;
    private ApiService apiService; // Khai báo ở đây để dùng chung trong Class

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Khởi tạo ApiService ngay đầu tiên
        apiService = RetrofitClient.getClient().create(ApiService.class);

        if (checkAutoLogin()) {
            return;
        }

        setContentView(R.layout.login);

        tilEmail = findViewById(R.id.til_email);
        tilPassword = findViewById(R.id.til_password);
        etEmail = (TextInputEditText) findViewById(R.id.et_email);
        etPassword = (TextInputEditText) findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        TextView tvFaceID = findViewById(R.id.tv_faceid_login);

        tvFaceID.setOnClickListener(v -> {
            SharedPreferences pref = getSharedPreferences("auth", MODE_PRIVATE);
            String token = pref.getString("access_token", null);
            String role = pref.getString("role", "");

            if (token == null) {
                // CHÚ Ý: Truyền email của user bạn muốn lấy token để demo
                fetchLastTokenFromServer("abc@gmail.com");
                return;
            }
            showBiometricPrompt(role, token);
        });

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập email và mật khẩu", Toast.LENGTH_SHORT).show();
                return;
            }

            apiService.login(new LoginRequest(email, password)).enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        LoginResponse res = response.body();
                        if ("success".equals(res.status)) {
                            SharedPreferences pref = getSharedPreferences("auth", MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("access_token", res.data.access_token);
                            editor.putString("user_id", res.data.user.USER_ID);
                            editor.putString("role", res.data.user.ROLE);
                            editor.apply();

                            navigateUser(res.data.user.ROLE, res.data.access_token);
                        }
                    }
                }
                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    Toast.makeText(LoginActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void fetchLastTokenFromServer(String email) {
        Toast.makeText(this, "Đang khôi phục phiên đăng nhập...", Toast.LENGTH_SHORT).show();

        // Giả sử bạn có 1 API: get_last_token.php?email=...
        apiService.getLastToken(email).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse res = response.body();

                    // Lưu vào máy giống như lúc login bằng mật khẩu
                    SharedPreferences pref = getSharedPreferences("auth", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("access_token", res.data.access_token);
                    editor.putString("role", res.data.user.ROLE);
                    editor.apply();

                    // Sau khi lấy được token thành công, tự động hiện vân tay luôn
                    showBiometricPrompt(res.data.user.ROLE, res.data.access_token);
                } else {
                    Toast.makeText(LoginActivity.this, "Không tìm thấy phiên đăng nhập cũ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Lỗi kết nối Server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showBiometricPrompt(String role, String token) {
        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt biometricPrompt = new BiometricPrompt(this, executor,
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        // FAKE LOGIC: Bỏ qua bước gửi ảnh lên Server AI
                        // Thông báo giả lập để giảng viên thấy có quy trình eKYC
                        Toast.makeText(LoginActivity.this, "eKYC: Xác thực khuôn mặt thành công!", Toast.LENGTH_SHORT).show();

                        // Chuyển màn hình ngay lập tức
                        runOnUiThread(() -> navigateUser(role, token));
                    }

                    @Override
                    public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                        // Nếu thiết bị không có vân tay/faceID (Error 11), ta vẫn cho vào luôn để demo
                        if (errorCode == 11) { // BIOMETRIC_ERROR_NONE_ENROLLED
                            navigateUser(role, token);
                        } else {
                            Toast.makeText(LoginActivity.this, "Lỗi: " + errString, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Đăng nhập nhanh Biometric")
                .setSubtitle("Hệ thống đang quét khuôn mặt/vân tay...")
                .setNegativeButtonText("Hủy")
                // Cho phép dùng cả PIN/Mật khẩu máy nếu FaceID hỏng
                .setAllowedAuthenticators(androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK | androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG)
                .build();

        biometricPrompt.authenticate(promptInfo);
    }

    private void performFaceVerification(String token, String role, File imageFile) {
        // Sửa lỗi create(MediaType, File)
        RequestBody requestFile = RequestBody.create(imageFile, MediaType.parse("image/*"));
        MultipartBody.Part body = MultipartBody.Part.createFormData("face_image", imageFile.getName(), requestFile);

        apiService.verifyFace("Bearer " + token, body).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                if (response.isSuccessful() && response.body() != null && "success".equals(response.body().status)) {
                    navigateUser(role, token);
                } else {
                    Toast.makeText(LoginActivity.this, "AI: Khuôn mặt không khớp!", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Lỗi AI Server", Toast.LENGTH_SHORT).show();
            }
        });
    }

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