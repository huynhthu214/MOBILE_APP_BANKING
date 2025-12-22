package com.example.zybanking.ui.transaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.zybanking.R;
import com.example.zybanking.data.models.account.AccountSummaryResponse;
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
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        tvBalance = findViewById(R.id.tv_detail_balance);
        tvPrincipal = findViewById(R.id.tv_detail_principal);
        tvRate = findViewById(R.id.tv_detail_rate);
        tvProfit = findViewById(R.id.tv_detail_profit);
        tvMaturity = findViewById(R.id.tv_detail_maturity);
        tvAccNo = findViewById(R.id.tv_saving_acc_no);
        imgToggleAcc = findViewById(R.id.img_toggle_saving_acc);

        btnDeposit = findViewById(R.id.btn_save_deposit);
        btnWithdraw = findViewById(R.id.btn_save_withdraw);

        if (btnDeposit != null) {
            btnDeposit.setOnClickListener(v -> {
                Intent intent = new Intent(SavingsActivity.this, DepositSavingActivity.class);
                intent.putExtra("ACCOUNT_ID", accountId);
                startActivity(intent);
            });
        }

        if (btnWithdraw != null) {
            btnWithdraw.setOnClickListener(v -> {
                Intent intent = new Intent(SavingsActivity.this, WithdrawSavingActivity.class);
                intent.putExtra("ACCOUNT_ID", accountId);
                startActivity(intent);
            });
        }

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
                    // TRUYỀN TOÀN BỘ RESPONSE VÀO UPDATEUI
                    updateUI(response.body());
                } else {
                    Toast.makeText(SavingsActivity.this, "Lỗi tải dữ liệu tiết kiệm", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AccountSummaryResponse> call, Throwable t) {
                Log.e("SavingsActivity", "Error: " + t.getMessage());
            }
        });
    }

    private void updateUI(AccountSummaryResponse response) {
        // 1. Kiểm tra an toàn vì dữ liệu nằm trong response.data
        if (response == null || response.data == null) {
            Log.e("SavingsActivity", "updateUI: Data is null");
            return;
        }

        // 2. Lấy object chứa dữ liệu thực tế (Lấy từ AccountData đã sửa ở file Model)
        AccountSummaryResponse.AccountData actualData = response.data;

        // 3. Cập nhật số tài khoản
        if (actualData.accountNumber != null) {
            realAccNo = actualData.accountNumber;
            updateAccNoDisplay();
        }

        // 4. Cập nhật các thông số tiền tệ
        if (tvBalance != null) {
            tvBalance.setText(formatCurrency(actualData.balance));
        }

        if (tvPrincipal != null) {
            // Nếu có tiền gốc riêng thì hiện, không thì hiện số dư
            Double principal = (actualData.principalAmount != null) ? actualData.principalAmount : actualData.balance;
            tvPrincipal.setText(formatCurrency(principal));
        }

        if (tvRate != null && actualData.interestRate != null) {
            // Giả sử server trả về 0.05 nghĩa là 5%
            tvRate.setText((actualData.interestRate * 100) + "% / Năm");
        }

        if (tvProfit != null && actualData.monthlyInterest != null) {
            tvProfit.setText("+" + formatCurrency(actualData.monthlyInterest));
        } else if (tvProfit != null) {
            tvProfit.setText("+0 VND");
        }

        if (tvMaturity != null && actualData.maturityDate != null) {
            tvMaturity.setText(formatDate(actualData.maturityDate));
        }
    }

    // --- CÁC HÀM HELPER GIỮ NGUYÊN ---

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
        String[] formats = {"EEE, dd MMM yyyy HH:mm:ss 'GMT'", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd"};
        for (String format : formats) {
            try {
                java.text.SimpleDateFormat inputFormat = new java.text.SimpleDateFormat(format, Locale.ENGLISH);
                if (format.contains("GMT")) inputFormat.setTimeZone(java.util.TimeZone.getTimeZone("GMT"));
                return inputFormat.parse(dateString);
            } catch (Exception ignored) {}
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

    private String formatCurrency(Double amount) {
        if (amount == null) return "0 VND";
        return NumberFormat.getInstance(new Locale("vi", "VN")).format(amount) + " VND";
    }

    private String formatAccountNumber(String accNum) {
        if (accNum == null) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < accNum.length(); i++) {
            if (i > 0 && i % 4 == 0) sb.append(" ");
            sb.append(accNum.charAt(i));
        }
        return sb.toString();
    }
}