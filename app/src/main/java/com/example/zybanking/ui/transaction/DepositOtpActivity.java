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

        apiService = RetrofitClient.getClient().create(ApiService.class);

        // ===== Lấy transaction_id từ Intent =====
        transactionId = getIntent().getStringExtra("transaction_id");

        // Validate đầu vào
        if (transactionId == null || transactionId.isEmpty()) {
            Toast.makeText(this, "Lỗi: Không tìm thấy mã giao dịch", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // ===== Sự kiện Click =====
        btnVerify.setOnClickListener(v -> confirmOtp());
        tvBack.setOnClickListener(v -> finish());

        // TODO: Gọi API resend OTP tại đây nếu cần
        tvResend.setOnClickListener(v ->
                Toast.makeText(this, "Chức năng gửi lại OTP chưa hỗ trợ", Toast.LENGTH_SHORT).show()
        );
    }

    private void confirmOtp() {
        String otp = edtOtp.getText().toString().trim();

        if (otp.length() != 6) {
            Toast.makeText(this, "Mã OTP phải đủ 6 chữ số", Toast.LENGTH_SHORT).show();
            return;
        }

        // 1. Khóa nút bấm để tránh double-click
        btnVerify.setEnabled(false);
        btnVerify.setText("Đang xử lý...");

        OtpConfirmRequest request = new OtpConfirmRequest(transactionId, otp);

        apiService.depositConfirm(request).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                // Mở lại nút bấm dù thành công hay thất bại
                btnVerify.setEnabled(true);
                btnVerify.setText("Xác thực");

                if (!response.isSuccessful()) {
                    Toast.makeText(DepositOtpActivity.this, "Lỗi server: " + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }

                BasicResponse body = response.body();
                if (body == null) {
                    Toast.makeText(DepositOtpActivity.this, "Phản hồi rỗng từ Server", Toast.LENGTH_SHORT).show();
                    return;
                }

                // ===== XỬ LÝ THÀNH CÔNG =====
                if ("success".equalsIgnoreCase(body.status)) {
                    Toast.makeText(DepositOtpActivity.this, "Nạp tiền thành công!", Toast.LENGTH_LONG).show();

                    // 1. Đảm bảo đích đến là MainActivity (Trang chủ)
                    Intent intent = new Intent(DepositOtpActivity.this, HomeActivity.class);

                    // 2. Dùng cờ này: Nếu MainActivity đang chạy ngầm, nó sẽ lôi lên và đóng các trang khác (Deposit, OTP)
                    // Cách này giữ nguyên được trạng thái đăng nhập tốt hơn
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                    startActivity(intent);
                    finish();
                } else {
                    // Trường hợp OTP sai hoặc hết hạn
                    Toast.makeText(DepositOtpActivity.this, body.message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                btnVerify.setEnabled(true);
                btnVerify.setText("Xác thực");
                Toast.makeText(DepositOtpActivity.this, "Lỗi kết nối mạng!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}