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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DepositOtpActivity extends AppCompatActivity {

    TextInputEditText edtOtp;
    MaterialButton btnVerify;
    TextView tvBack, tvResend;

    ApiService apiService;
    String transactionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otp_verify);

        // ===== Bind view =====
        edtOtp = findViewById(R.id.edt_otp);
        btnVerify = findViewById(R.id.btn_verify);
        tvBack = findViewById(R.id.tv_back);
        tvResend = findViewById(R.id.tv_resend);

        apiService = RetrofitClient
                .getClient()
                .create(ApiService.class);

        // ===== Lấy transaction_id từ Intent =====
        transactionId = getIntent().getStringExtra("transaction_id");

        if (transactionId == null) {
            Toast.makeText(this, "Thiếu transaction id", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // ===== Click =====
        btnVerify.setOnClickListener(v -> confirmOtp());
        tvBack.setOnClickListener(v -> finish());

        tvResend.setOnClickListener(v ->
                Toast.makeText(this, "Chức năng gửi lại OTP chưa hỗ trợ", Toast.LENGTH_SHORT).show()
        );
    }

    private void confirmOtp() {
        String otp = edtOtp.getText().toString().trim();

        if (otp.length() != 6) {
            Toast.makeText(this, "OTP phải đủ 6 chữ số", Toast.LENGTH_SHORT).show();
            return;
        }

        OtpConfirmRequest request =
                new OtpConfirmRequest(transactionId, otp);

        apiService.depositConfirm(request)
                .enqueue(new Callback<BasicResponse>() {
                    @Override
                    public void onResponse(
                            Call<BasicResponse> call,
                            Response<BasicResponse> response
                    ) {
                        if (!response.isSuccessful()) {
                            Toast.makeText(
                                    DepositOtpActivity.this,
                                    "HTTP lỗi: " + response.code(),
                                    Toast.LENGTH_SHORT
                            ).show();
                            return;
                        }

                        BasicResponse body = response.body();

                        if (body == null) {
                            Toast.makeText(
                                    DepositOtpActivity.this,
                                    "Response rỗng",
                                    Toast.LENGTH_SHORT
                            ).show();
                            return;
                        }

                        if ("success".equals(body.status)) {
                            Toast.makeText(
                                    DepositOtpActivity.this,
                                    "Nạp tiền thành công 🎉",
                                    Toast.LENGTH_SHORT
                            ).show();

                            finish(); // hoặc quay về màn home
                        } else {
                            Toast.makeText(
                                    DepositOtpActivity.this,
                                    body.message,
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<BasicResponse> call, Throwable t) {
                        Toast.makeText(
                                DepositOtpActivity.this,
                                "Lỗi kết nối backend",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }
}
