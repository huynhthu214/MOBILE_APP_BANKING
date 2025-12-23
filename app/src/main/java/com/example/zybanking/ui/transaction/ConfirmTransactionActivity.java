package com.example.zybanking.ui.transaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

    // Khai báo view
    private LinearLayout layoutOtpContainer;
    private EditText etPin, etOtp;
    private Button btnConfirm;
    private boolean isPinVerified = false; // Biến cờ để kiểm soát trạng thái

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirm_transaction);

        apiService = RetrofitClient.getClient().create(ApiService.class);

        sourceId = getIntent().getStringExtra("SOURCE_ID");
        recipientId = getIntent().getStringExtra("RECIPIENT_ID");
        amount = getIntent().getDoubleExtra("AMOUNT", 0);

        initViews();
    }

    private void initViews() {
        etPin = findViewById(R.id.et_pin_confirm);
        etOtp = findViewById(R.id.et_otp_confirm);
        layoutOtpContainer = findViewById(R.id.layout_otp_container);
        btnConfirm = findViewById(R.id.btn_confirm_dialog);

        TextView tvAmount = findViewById(R.id.tv_confirm_amount);
        TextView tvDest = findViewById(R.id.tv_confirm_dest);
        Button btnCancel = findViewById(R.id.btn_cancel_dialog);

        tvAmount.setText(java.text.NumberFormat.getInstance().format(amount) + " VND");
        tvDest.setText(recipientId);

        btnCancel.setOnClickListener(v -> finish());

        btnConfirm.setOnClickListener(v -> {
            if (!isPinVerified) {
                // Bước 1: Xác thực PIN
                String pin = etPin.getText().toString().trim();
                if (pin.length() == 6) {
                    verifyPinAndSendOtp(pin);
                } else {
                    Toast.makeText(this, "Vui lòng nhập đủ 6 số PIN", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Bước 2: Xác thực OTP
                String otp = etOtp.getText().toString().trim();
                if (otp.length() == 6) {
                    verifyTransfer(otp);
                } else {
                    Toast.makeText(this, "Vui lòng nhập đủ 6 số OTP", Toast.LENGTH_SHORT).show();
                }
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
                    // PIN ĐÚNG -> Hiển thị ô nhập OTP
                    isPinVerified = true;
                    layoutOtpContainer.setVisibility(View.VISIBLE); // Hiện layout OTP
                    etPin.setEnabled(false); // Khóa ô nhập PIN lại
                    btnConfirm.setText("Xác nhận OTP"); // Đổi chữ trên nút để người dùng dễ hiểu

                    Toast.makeText(ConfirmTransactionActivity.this,
                            "PIN đúng. OTP đã được gửi về Email", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ConfirmTransactionActivity.this,
                            "PIN không chính xác", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                Toast.makeText(ConfirmTransactionActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void verifyTransfer(String otp) {
        String txId = getIntent().getStringExtra("TX_ID");
        OtpConfirmRequest confirmRequest = new OtpConfirmRequest(txId, otp);

        apiService.transferConfirm(confirmRequest).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                if (response.isSuccessful()) {
                    Intent intent = new Intent(ConfirmTransactionActivity.this, TransferSuccessActivity.class);
                    intent.putExtra("AMOUNT", amount);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(ConfirmTransactionActivity.this, "OTP không chính xác", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                Toast.makeText(ConfirmTransactionActivity.this, "Lỗi xác thực", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
