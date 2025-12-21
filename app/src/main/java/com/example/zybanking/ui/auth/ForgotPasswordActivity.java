package com.example.zybanking.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.zybanking.R;
import com.example.zybanking.data.models.auth.ForgotPasswordRequest;
import com.example.zybanking.data.models.auth.ForgotPasswordResponse;
import com.example.zybanking.data.remote.ApiService;
import com.example.zybanking.data.remote.RetrofitClient;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextInputLayout tilEmail;
    private TextInputEditText etEmail;
    private Button btnSendCode;
    private TextView tvBackLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);

        // 1. Ánh xạ View
        tilEmail = findViewById(R.id.til_email);
        // Lấy EditText bên trong TextInputLayout
        if (tilEmail.getEditText() != null) {
            etEmail = (TextInputEditText) tilEmail.getEditText();
        } else {
            // Fallback nếu không tìm thấy, dù thường sẽ có nếu XML đúng
            etEmail = findViewById(R.id.edt_email);
        }

        btnSendCode = findViewById(R.id.btn_send_code);
        tvBackLogin = findViewById(R.id.tv_back_login);

        // 2. Sự kiện nút Quay lại
        tvBackLogin.setOnClickListener(v -> finish());

        // 3. Sự kiện nút Gửi mã
        btnSendCode.setOnClickListener(v -> handleSendCode());
    }

    private void handleSendCode() {
        String email = etEmail.getText().toString().trim();

        // Validate: Không được để trống
        if (email.isEmpty()) {
            tilEmail.setError("Vui lòng nhập email");
            return;
        } else {
            tilEmail.setError(null);
        }

        // Tạo request model
        ForgotPasswordRequest request = new ForgotPasswordRequest(email);

        // Gọi API
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        apiService.forgotPassword(request).enqueue(new Callback<ForgotPasswordResponse>() {
            @Override
            public void onResponse(Call<ForgotPasswordResponse> call, Response<ForgotPasswordResponse> response) {
                // Kiểm tra phản hồi thành công (HTTP 200)
                if (response.isSuccessful() && response.body() != null) {
                    ForgotPasswordResponse res = response.body();
                    if ("success".equalsIgnoreCase(res.status)) {
                        Intent intent = new Intent(ForgotPasswordActivity.this, OtpVerificationActivity.class);
                        intent.putExtra("user_id", res.data.user_id); // backend phải trả
                        startActivity(intent);
                    }else {
                        // Server trả về 200 nhưng logic lỗi (ví dụ email ko tồn tại)
                        Toast.makeText(ForgotPasswordActivity.this, res.message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Lỗi HTTP (404, 500...)
                    Toast.makeText(ForgotPasswordActivity.this, "Gửi thất bại. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ForgotPasswordResponse> call, Throwable t) {
                Log.e("ForgotPass", "API Error: " + t.getMessage());
                Toast.makeText(ForgotPasswordActivity.this, "Lỗi kết nối mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }
}