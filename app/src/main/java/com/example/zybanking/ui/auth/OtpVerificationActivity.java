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
import com.example.zybanking.data.models.auth.VerifyOtpRequest;
import com.example.zybanking.data.remote.ApiService;
import com.example.zybanking.data.remote.RetrofitClient;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.Map;

public class OtpVerificationActivity extends AppCompatActivity {
    private TextView tvBack;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otp_verify);

        userId = getIntent().getStringExtra("user_id");

        if (userId == null) {
            Toast.makeText(this, "Thiếu dữ liệu người dùng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        tvBack = findViewById(R.id.tv_back);
        Button btnVerify = findViewById(R.id.btn_verify);
        TextInputEditText edtOtp = findViewById(R.id.edt_otp);

        tvBack.setOnClickListener(v -> finish());
        btnVerify.setOnClickListener(v -> {
            String otpCode = edtOtp.getText().toString().trim();

            if (otpCode.length() != 6) {
                Toast.makeText(this, "Vui lòng nhập đủ 8 chữ số", Toast.LENGTH_SHORT).show();
                return;
            }

            ApiService api = RetrofitClient.getClient().create(ApiService.class);
            VerifyOtpRequest request = new VerifyOtpRequest(userId, otpCode);

            api.verifyOtp(request).enqueue(new Callback<ForgotPasswordResponse>() {
                @Override
                public void onResponse(Call<ForgotPasswordResponse> call,
                                       Response<ForgotPasswordResponse> response) {

                    Toast.makeText(
                            OtpVerificationActivity.this,
                            "HTTP " + response.code(),
                            Toast.LENGTH_SHORT
                    ).show();

                    if (!response.isSuccessful()) {
                        Toast.makeText(
                                OtpVerificationActivity.this,
                                "Verify OTP failed",
                                Toast.LENGTH_SHORT
                        ).show();
                        return;
                    }

                    ForgotPasswordResponse res = response.body();
                    if (res == null) {
                        Toast.makeText(
                                OtpVerificationActivity.this,
                                "Empty response",
                                Toast.LENGTH_SHORT
                        ).show();
                        return;
                    }

                    if ("success".equalsIgnoreCase(res.status)) {
                        Intent intent = new Intent(
                                OtpVerificationActivity.this,
                                ResetPasswordActivity.class
                        );
                        intent.putExtra("user_id", userId);
                        intent.putExtra("otp_code", otpCode);
                        startActivity(intent);
                    } else {
                        Toast.makeText(
                                OtpVerificationActivity.this,
                                res.message,
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }

                @Override
                public void onFailure(Call<ForgotPasswordResponse> call, Throwable t) {
                    Toast.makeText(
                            OtpVerificationActivity.this,
                            "Lỗi mạng: " + t.getMessage(),
                            Toast.LENGTH_SHORT
                    ).show();
                }
            });

        });

        TextView tvResend = findViewById(R.id.tv_resend);
        tvResend.setOnClickListener(v -> {
            ApiService api = RetrofitClient.getClient().create(ApiService.class);

            Map<String, String> body = new HashMap<>();
            body.put("user_id", userId);

            api.resendOtp(body).enqueue(new Callback<ForgotPasswordResponse>() {
                @Override
                public void onResponse(Call<ForgotPasswordResponse> call, Response<ForgotPasswordResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        ForgotPasswordResponse res = response.body();
                        if ("success".equalsIgnoreCase(res.status)) {
                            Toast.makeText(OtpVerificationActivity.this, "Mã OTP mới đã được gửi", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(OtpVerificationActivity.this, res.message, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(OtpVerificationActivity.this, "Gửi lại OTP thất bại", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ForgotPasswordResponse> call, Throwable t) {
                    Toast.makeText(OtpVerificationActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}