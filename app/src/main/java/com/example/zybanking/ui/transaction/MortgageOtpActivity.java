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
import com.example.zybanking.ui.dashboard.HomeActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MortgageOtpActivity extends AppCompatActivity {

    TextInputEditText edtOtp;
    MaterialButton btnVerify;
    TextView tvBack, tvTitle;

    ApiService apiService;
    String transactionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Tái sử dụng layout OTP dùng chung
        setContentView(R.layout.otp_verify);

        edtOtp = findViewById(R.id.edt_otp);
        btnVerify = findViewById(R.id.btn_verify);
        tvBack = findViewById(R.id.tv_back);
        tvTitle = findViewById(R.id.tv_title); // Giả sử layout bạn có tv_title để đổi tên màn hình

        if (tvTitle != null) {
            tvTitle.setText("Xác thực thanh toán vay");
        }

        apiService = RetrofitClient.getClient().create(ApiService.class);

        // Nhận transaction_id từ Intent (do màn hình trước đó gửi sang)
        transactionId = getIntent().getStringExtra("transaction_id");

        tvBack.setOnClickListener(v -> finish());

        btnVerify.setOnClickListener(v -> {
            String otp = edtOtp.getText().toString().trim();
            if (otp.length() == 6) {
                confirmMortgagePayment(otp);
            } else {
                Toast.makeText(this, "Vui lòng nhập đủ 6 chữ số OTP", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmMortgagePayment(String otp) {
        btnVerify.setEnabled(false);
        btnVerify.setText("Đang xử lý...");

        // Gửi transaction_id và mã otp lên server
        OtpConfirmRequest request = new OtpConfirmRequest(transactionId, otp);

        // Gọi API CONFIRM THANH TOÁN THẾ CHẤP (Bạn cần định nghĩa hàm này trong ApiService)
        apiService.mortgagePaymentConfirm(request).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                btnVerify.setEnabled(true);
                btnVerify.setText("Xác thực");

                if (response.isSuccessful() && response.body() != null) {
                    BasicResponse body = response.body();
                    if ("success".equals(body.status)) {
                        Toast.makeText(MortgageOtpActivity.this, "Thanh toán khoản vay thành công!", Toast.LENGTH_LONG).show();

                        // Sau khi thành công, xóa các activity cũ và về MainActivity
                        Intent intent = new Intent(MortgageOtpActivity.this, HomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(MortgageOtpActivity.this, body.message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MortgageOtpActivity.this, "Xác thực thất bại, vui lòng thử lại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                btnVerify.setEnabled(true);
                btnVerify.setText("Xác thực");
                Toast.makeText(MortgageOtpActivity.this, "Lỗi kết nối server", Toast.LENGTH_SHORT).show();
            }
        });
    }
}