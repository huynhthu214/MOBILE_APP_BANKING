package com.example.zybanking.ui.transaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.zybanking.R;
import com.example.zybanking.data.models.AccountSummaryResponse;
import com.example.zybanking.data.remote.ApiService;
import com.example.zybanking.data.remote.RetrofitClient;

import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SavingsActivity extends AppCompatActivity {
    private ImageView btnBack, imgToggleAcc;
    private TextView tvBalance, tvPrincipal, tvRate, tvProfit, tvAccNo, tvMaturity;
    private boolean isHidden = true;
    private String realAccNo = "";
    private String accountId;
    private Button btnDeposit, btnWithdraw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.basic_savings);
        // Nhận Account ID từ Intent
        accountId = getIntent().getStringExtra("ACCOUNT_ID");
        initViews();
        if (accountId != null) {
            loadData();
        } else {
            Toast.makeText(this, "Không tìm thấy tài khoản!", Toast.LENGTH_SHORT).show();
        }
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // Ánh xạ các TextView trong basic_savings.xml
        tvBalance = findViewById(R.id.tv_detail_balance);     // Tổng tiền
        tvPrincipal = findViewById(R.id.tv_detail_principal); // Tiền gốc
        tvRate = findViewById(R.id.tv_detail_rate);           // Lãi suất
        tvProfit = findViewById(R.id.tv_detail_profit);       // Lợi nhuận
        tvMaturity = findViewById(R.id.tv_detail_maturity);   // Ngày đáo hạn
        btnDeposit = findViewById(R.id.btn_save_deposit);
        btnWithdraw = findViewById(R.id.btn_save_withdraw);
        // Xử lý sự kiện Gửi thêm -> Chuyển sang DepositActivity
        btnDeposit.setOnClickListener(v -> {
            Intent intent = new Intent(SavingsActivity.this, DepositSavingActivity.class);
            intent.putExtra("ACCOUNT_ID", accountId); // Truyền ID để nạp đúng tk
            startActivity(intent);
        });

        // Xử lý sự kiện Rút/Tất toán -> Chuyển sang WithdrawActivity
        btnWithdraw.setOnClickListener(v -> {
                    Intent intent = new Intent(SavingsActivity.this, WithdrawSavingActivity.class);
                    intent.putExtra("ACCOUNT_ID", accountId);
                    startActivity(intent);
                });
        // Phần số tài khoản
        tvAccNo = findViewById(R.id.tv_saving_acc_no);        // TextView số TK (thêm id này vào xml nếu chưa có)
        imgToggleAcc = findViewById(R.id.img_toggle_saving_acc); // Icon mắt

        if (imgToggleAcc != null) {
            imgToggleAcc.setOnClickListener(v -> {
                isHidden = !isHidden;
                updateAccNoDisplay();
            });
        }
    }
    private void loadData() {
        ApiService api = RetrofitClient.getClient().create(ApiService.class);
        api.getAccountSummary(accountId).enqueue(new Callback<AccountSummaryResponse>() {
            @Override
            public void onResponse(Call<AccountSummaryResponse> call, Response<AccountSummaryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AccountSummaryResponse data = response.body();
                    updateUI(data);
                } else {
                    Toast.makeText(SavingsActivity.this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<AccountSummaryResponse> call, Throwable t) {
                Log.e("SavingsActivity", "Error: " + t.getMessage());
            }
        });
    }

    private void updateUI(AccountSummaryResponse data) {
        // 1. Cập nhật số tài khoản thực để dùng cho chức năng ẩn/hiện
        if (data.accountNumber != null) {
            realAccNo = data.accountNumber;
            updateAccNoDisplay();
        }
        // 2. Cập nhật các thông số tiền tệ
        if (tvBalance != null) tvBalance.setText(formatCurrency(data.balance));
        // Lưu ý: data.principalAmount cần backend trả về. Nếu chưa có, tạm dùng balance hoặc yêu cầu backend thêm.
        if (tvPrincipal != null) {
            Double principal = (data.principalAmount != null) ? data.principalAmount : data.balance;
            tvPrincipal.setText(formatCurrency(principal));
        }
        if (tvRate != null && data.interestRate != null) {
            tvRate.setText((data.interestRate * 100) + "% / Năm");
        }
        if (tvProfit != null && data.monthlyInterest != null) {
            tvProfit.setText("+" + formatCurrency(data.monthlyInterest));
        }
        if (tvMaturity != null && data.maturityDate != null) {
            // Dùng hàm formatDate chuẩn Timezone
            tvMaturity.setText(formatDate(data.maturityDate));
        }
    }
    private String formatDate(String dateString) {
        Date date = parseDateString(dateString);
        if (date != null) {
            java.text.SimpleDateFormat outputFormat = new java.text.SimpleDateFormat("dd/MM/yyyy", new Locale("vi", "VN"));
            return outputFormat.format(date);
        }
        return dateString;
    }
    private Date parseDateString(String dateString) {
        if (dateString == null || dateString.isEmpty()) return null;
        String[] formats = {
                "EEE, dd MMM yyyy HH:mm:ss 'GMT'",
                "yyyy-MM-dd HH:mm:ss",
                "yyyy-MM-dd"
        };
        for (String format : formats) {
            try {
                java.text.SimpleDateFormat inputFormat = new java.text.SimpleDateFormat(format, Locale.ENGLISH);
                if (format.contains("GMT")) {
                    inputFormat.setTimeZone(java.util.TimeZone.getTimeZone("GMT"));
                }
                return inputFormat.parse(dateString);
            } catch (Exception e) {
                continue;
            }
        }
        return null;
    }
    private void updateAccNoDisplay() {
        if (tvAccNo == null) return;

        if (isHidden) {
            String masked = "**** **** **** " + (realAccNo.length() > 4 ? realAccNo.substring(realAccNo.length() - 4) : "****");
            tvAccNo.setText(masked);
            imgToggleAcc.setImageResource(R.drawable.ic_eye_off);
        } else {
            tvAccNo.setText(formatAccountNumber(realAccNo));
            imgToggleAcc.setImageResource(R.drawable.ic_eye_on);
        }
    }
    // Helper format tiền
    private String formatCurrency(Double amount) {
        if (amount == null) return "0 VND";
        return NumberFormat.getInstance(new Locale("vi", "VN")).format(amount) + " VND";
    }
    // Helper format số tài khoản có khoảng trắng
    private String formatAccountNumber(String accNum) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < accNum.length(); i++) {
            if (i > 0 && i % 4 == 0) sb.append(" ");
            sb.append(accNum.charAt(i));
        }
        return sb.toString();
    }
}