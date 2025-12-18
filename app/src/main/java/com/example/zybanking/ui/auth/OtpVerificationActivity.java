package com.example.zybanking.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import com.example.zybanking.R;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class OtpVerificationActivity extends AppCompatActivity {
    // ... khai báo view
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otp_verify);

        // Nhận USER_ID từ màn hình trước
        userId = getIntent().getStringExtra("USER_ID");

        // ... ánh xạ view (edtOtp, btnVerify...)

        Button btnVerify = findViewById(R.id.btn_verify);
        TextInputEditText edtOtp = findViewById(R.id.edt_otp);

        btnVerify.setOnClickListener(v -> {
            String otpCode = edtOtp.getText().toString().trim();
            if(otpCode.length() < 6) {
                Toast.makeText(this, "Vui lòng nhập đủ 6 số", Toast.LENGTH_SHORT).show();
                return;
            }

            // CHUYỂN TIẾP SANG MÀN HÌNH ĐẶT PASS (Mang theo USER_ID và OTP_CODE)
            Intent intent = new Intent(OtpVerificationActivity.this, ResetPasswordActivity.class);
            intent.putExtra("USER_ID", userId);
            intent.putExtra("OTP_CODE", otpCode);
            startActivity(intent);
        });
    }
}
