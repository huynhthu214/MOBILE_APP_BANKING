package com.example.zybanking;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivity extends AppCompatActivity {

    private TextView tvUserName, tvCurrentBalance;

    // Các nút dịch vụ chính
    private LinearLayout btnPayBills, btnPhoneTopup, btnLoan;
    private LinearLayout btnSavings, btnMortgage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

        // ===== Ánh xạ view =====
        tvUserName = findViewById(R.id.tv_user_name);
        tvCurrentBalance = findViewById(R.id.tv_current_balance);

        btnPayBills   = findViewById(R.id.btn_pay_bills);
        btnPhoneTopup = findViewById(R.id.btn_phone_topup);
        btnLoan       = findViewById(R.id.btn_loan);
        btnSavings    = findViewById(R.id.btn_savings);
        btnMortgage   = findViewById(R.id.btn_mortgage);

        // ===== Nhận dữ liệu từ LoginActivity =====
        String userName = getIntent().getStringExtra("user_name");
        if (userName != null && !userName.isEmpty()) {
            tvUserName.setText(userName + "!");
        }

        // ===== Dữ liệu fake (sau này thay bằng API) =====
        tvCurrentBalance.setText("4.570.800 VND");

        // ===== Bắt sự kiện click =====
        btnPayBills.setOnClickListener(v ->
                showToast("Chức năng thanh toán hóa đơn")
        );

        btnPhoneTopup.setOnClickListener(v ->
                showToast("Chức năng nạp tiền điện thoại")
        );

        btnLoan.setOnClickListener(v ->
                showToast("Chức năng vay tiền")
        );

        btnSavings.setOnClickListener(v ->
                showToast("Chức năng tiết kiệm")
        );

        btnMortgage.setOnClickListener(v ->
                showToast("Chức năng thế chấp")
        );
    }

    private void showToast(String message) {
        Toast.makeText(this, message + " chưa triển khai", Toast.LENGTH_SHORT).show();
    }
}
