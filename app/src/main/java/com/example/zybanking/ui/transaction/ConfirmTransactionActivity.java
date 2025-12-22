package com.example.zybanking.ui.transaction;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.zybanking.data.models.OtpConfirmRequest;
import com.example.zybanking.data.models.transaction.VerifyPinRequest;
import com.example.zybanking.ui.dashboard.HomeActivity;
import com.example.zybanking.R;
import com.example.zybanking.data.models.BasicResponse;
import com.example.zybanking.data.models.transaction.WithdrawRequest;
import com.example.zybanking.data.remote.ApiService;
import com.example.zybanking.data.remote.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfirmTransactionActivity extends AppCompatActivity {
    private ApiService apiService;
    private String sourceId, recipientId;
    private double amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirm_transaction);

        apiService = RetrofitClient.getClient().create(ApiService.class);

        // Nhận dữ liệu từ Intent
        sourceId = getIntent().getStringExtra("SOURCE_ID");
        recipientId = getIntent().getStringExtra("RECIPIENT_ID");
        amount = getIntent().getDoubleExtra("AMOUNT", 0);

        initViews();
        // Bước quan trọng: Gọi API để hệ thống gửi OTP về Email người dùng ngay khi vào trang
//        requestOtpEmail();
    }

    private void initViews() {
        EditText etPin = findViewById(R.id.et_pin_confirm);
        TextView tvAmount = findViewById(R.id.tv_confirm_amount);
        TextView tvDest = findViewById(R.id.tv_confirm_dest);
        EditText etOtp = findViewById(R.id.et_otp_confirm);
        Button btnConfirm = findViewById(R.id.btn_confirm_dialog);
        Button btnCancel = findViewById(R.id.btn_cancel_dialog);

        tvAmount.setText(java.text.NumberFormat.getInstance().format(amount) + " VND");
        tvDest.setText(recipientId);

        btnCancel.setOnClickListener(v -> finish());
        btnConfirm.setOnClickListener(v -> {
            String pin = etPin.getText().toString().trim();
            String otp = etOtp.getText().toString().trim();

            if (pin.length() != 6) {
                Toast.makeText(this, "Vui lòng nhập đủ 6 số PIN", Toast.LENGTH_SHORT).show();
                return;
            }

            // Nếu chưa nhập OTP → verify PIN & gửi OTP
            if (otp.isEmpty()) {
                verifyPinAndSendOtp(pin);
                return;
            }

            // Nếu đã có OTP → xác nhận giao dịch
            if (otp.length() == 6) {
                verifyTransfer(otp);
            } else {
                Toast.makeText(this, "OTP phải gồm 6 chữ số", Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void verifyPinAndSendOtp(String pin) {
        String txId = getIntent().getStringExtra("TX_ID");

        VerifyPinRequest request = new VerifyPinRequest(txId, pin);

        apiService.verifyPin(request).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(
                            ConfirmTransactionActivity.this,
                            "PIN đúng. OTP đã được gửi về Email",
                            Toast.LENGTH_SHORT
                    ).show();
                } else {
                    Toast.makeText(
                            ConfirmTransactionActivity.this,
                            "PIN không chính xác",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                Toast.makeText(
                        ConfirmTransactionActivity.this,
                        "Lỗi xác thực PIN",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    private void requestOtpEmail() {
        // Gọi API withdrawCreate với OTP null để kích hoạt gửi mail
        WithdrawRequest request = new WithdrawRequest(sourceId, amount, null);
        apiService.withdrawCreate(request).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ConfirmTransactionActivity.this, "Mã OTP đã được gửi về Email", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {}
        });
    }

    // Trong ConfirmTransactionActivity.java
    private void verifyTransfer(String otp) {
        String txId = getIntent().getStringExtra("TX_ID");

        // Gọi API transfer/confirm của bạn
        OtpConfirmRequest confirmRequest = new OtpConfirmRequest(txId, otp);
        apiService.transferConfirm(confirmRequest).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                if (response.isSuccessful()) {
                    // Chuyển sang màn hình thành công
                    Intent intent = new Intent(ConfirmTransactionActivity.this, TransferSuccessActivity.class);
                    intent.putExtra("AMOUNT", amount);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(ConfirmTransactionActivity.this, "Mã OTP không chính xác hoặc đã hết hạn", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                Toast.makeText(ConfirmTransactionActivity.this, "Lỗi xác thực", Toast.LENGTH_SHORT).show();
            }
        });
    }
}