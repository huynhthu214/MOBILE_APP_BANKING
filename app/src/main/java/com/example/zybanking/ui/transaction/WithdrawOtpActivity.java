package com.example.zybanking.ui.transaction;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.zybanking.R;
import com.example.zybanking.data.models.BasicResponse;
import com.example.zybanking.data.models.OtpConfirmRequest;
import com.example.zybanking.data.remote.ApiService;
import com.example.zybanking.data.remote.RetrofitClient;
import com.example.zybanking.MainActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WithdrawOtpActivity extends AppCompatActivity {

    TextInputEditText edtOtp;
    MaterialButton btnVerify;
    TextView tvBack;

    ApiService apiService;
    String transactionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Tái sử dụng layout OTP cũ (hoặc tạo layout otp_withdraw mới nếu muốn đổi text)
        setContentView(R.layout.otp_verify);

        edtOtp = findViewById(R.id.edt_otp);
        btnVerify = findViewById(R.id.btn_verify);
        tvBack = findViewById(R.id.tv_back);

        apiService = RetrofitClient.getClient().create(ApiService.class);
        transactionId = getIntent().getStringExtra("transaction_id");

        tvBack.setOnClickListener(v -> finish());

        btnVerify.setOnClickListener(v -> {
            String otp = edtOtp.getText().toString().trim();
            if (otp.length() == 6) {
                confirmWithdraw(otp);
            } else {
                Toast.makeText(this, "OTP phải đủ 6 số", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmWithdraw(String otp) {
        btnVerify.setEnabled(false);
        btnVerify.setText("Đang xác thực...");

        OtpConfirmRequest request = new OtpConfirmRequest(transactionId, otp);

        // Gọi API CONFIRM RÚT TIỀN
        apiService.withdrawConfirm(request).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                btnVerify.setEnabled(true);
                btnVerify.setText("Xác thực");

                if (response.isSuccessful() && response.body() != null) {
                    BasicResponse body = response.body();
                    if ("success".equals(body.status)) {
                        Toast.makeText(WithdrawOtpActivity.this, "Rút tiền thành công!", Toast.LENGTH_LONG).show();

                        // Quay về trang chủ (MainActivity)
                        Intent intent = new Intent(WithdrawOtpActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(WithdrawOtpActivity.this, body.message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(WithdrawOtpActivity.this, "Lỗi hệ thống", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                btnVerify.setEnabled(true);
                btnVerify.setText("Xác thực");
                Toast.makeText(WithdrawOtpActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }
}