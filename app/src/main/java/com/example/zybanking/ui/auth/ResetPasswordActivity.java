package com.example.zybanking.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.zybanking.R;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.example.zybanking.data.models.auth.ForgotPasswordResponse;
import com.example.zybanking.data.models.auth.ResetPasswordRequest;
import com.example.zybanking.data.remote.ApiService;
import com.example.zybanking.data.remote.RetrofitClient;
import com.google.android.material.textfield.TextInputEditText;

public class ResetPasswordActivity extends AppCompatActivity {
    private TextView tvBack;
    private String userId;
    private String otpCode;
    private TextInputEditText edtNewPass, edtConfirmPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_password);

        tvBack = findViewById(R.id.tv_back);
        userId = getIntent().getStringExtra("user_id");
        otpCode = getIntent().getStringExtra("otp_code");

        tvBack.setOnClickListener(v -> finish());
        if (userId == null || otpCode == null) {
            Toast.makeText(this, "Dữ liệu không hợp lệ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        edtNewPass = findViewById(R.id.edt_new_pass);
        edtConfirmPass = findViewById(R.id.edt_confirm_pass);
        Button btnReset = findViewById(R.id.btn_reset);

        btnReset.setOnClickListener(v -> {
            String newPass = edtNewPass.getText().toString().trim();
            String confirmPass = edtConfirmPass.getText().toString().trim();

            if (!newPass.equals(confirmPass)) {
                Toast.makeText(this, "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isValidPassword(newPass)) {
                Toast.makeText(
                        this,
                        "Mật khẩu phải ≥ 8 ký tự, có chữ hoa, số và ký tự đặc biệt",
                        Toast.LENGTH_LONG
                ).show();
                return;
            }

            performReset(newPass);
        });
    }


    private void performReset(String newPassword) {
        ResetPasswordRequest request = new ResetPasswordRequest(userId, otpCode, newPassword);

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        apiService.resetPassword(request).enqueue(new Callback<ForgotPasswordResponse>() {
            @Override
            public void onResponse(Call<ForgotPasswordResponse> call, Response<ForgotPasswordResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ForgotPasswordResponse res = response.body();
                    if ("success".equals(res.status)) {
                        Toast.makeText(ResetPasswordActivity.this, "Đổi mật khẩu thành công!", Toast.LENGTH_LONG).show();

                        // Về màn hình Login, xóa hết stack cũ
                        Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        // Lỗi nghiệp vụ (VD: Sai OTP)
                        Toast.makeText(ResetPasswordActivity.this, res.message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ResetPasswordActivity.this, "Lỗi xử lý (có thể sai OTP)", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ForgotPasswordResponse> call, Throwable t) {
                Toast.makeText(ResetPasswordActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private boolean isValidPassword(String password) {
        String regex = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        return password.matches(regex);
    }
}