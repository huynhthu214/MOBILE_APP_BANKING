package com.example.zybanking.ui.transaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

public class MortgageActivity extends AppCompatActivity {
    private ImageView btnBack, imgToggleAcc;
    private TextView tvPaymentAmount, tvDueDate, tvRemainingBalance, tvAccNo;
    private TextView tvFrequency, tvPaidAmount, tvTotalLoan;
    private ProgressBar pbLoanProgress;
    private boolean isHidden = true;
    private String realAccNo = "";
    private String accountId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.basic_mortgage);

        accountId = getIntent().getStringExtra("ACCOUNT_ID");

        initViews();
        if (accountId != null) {
            loadData();
        }
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBackMortgage);
        btnBack.setOnClickListener(v -> finish());

        // Ánh xạ theo basic_mortgage.xml
        tvPaymentAmount = findViewById(R.id.tv_payment_amount); // Số tiền phải trả kỳ này
        tvDueDate = findViewById(R.id.tv_mortgage_due_date);    // Ngày hết hạn (đổi ID trong xml cho khớp nếu cần)
        tvRemainingBalance = findViewById(R.id.tv_mortgage_remaining); // Dư nợ còn lại

        // Số tài khoản
        tvAccNo = findViewById(R.id.tv_mortgage_acc_no);        // Thêm ID này vào XML
        imgToggleAcc = findViewById(R.id.img_toggle_mortgage_acc); // Thêm ID này vào XML
        tvFrequency = findViewById(R.id.tv_frequency);
        tvPaidAmount = findViewById(R.id.tv_paid_amount);
        tvTotalLoan = findViewById(R.id.tv_total_loan);
        pbLoanProgress = findViewById(R.id.pb_loan_progress);

        if (imgToggleAcc != null) {
            imgToggleAcc.setOnClickListener(v -> {
                isHidden = !isHidden;
                updateAccNoDisplay();
            });
        }
        Button btnPay = findViewById(R.id.btn_pay_mortgage);
        btnPay.setOnClickListener(v -> {
            Intent intent = new Intent(MortgageActivity.this, MortgagePaymentActivity.class);
            intent.putExtra("ACCOUNT_ID", accountId); // Truyền ID khoản vay (ví dụ M004)
            startActivity(intent);
        });
    }

    private void loadData() {
        ApiService api = RetrofitClient.getClient().create(ApiService.class);
        api.getAccountSummary(accountId).enqueue(new Callback<AccountSummaryResponse>() {
            @Override
            public void onResponse(Call<AccountSummaryResponse> call, Response<AccountSummaryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    updateUI(response.body());
                }
            }
            @Override
            public void onFailure(Call<AccountSummaryResponse> call, Throwable t) {
                Log.e("MortgageActivity", "Error: " + t.getMessage());
            }
        });
    }

    private void updateUI(AccountSummaryResponse data) {
        if (data.accountNumber != null) {
            realAccNo = data.accountNumber;
            updateAccNoDisplay();
        }
        if (tvPaymentAmount != null) tvPaymentAmount.setText(formatCurrency(data.paymentAmount));

        if (tvDueDate != null && data.nextPaymentDate != null) {
            tvDueDate.setText(formatDate(data.nextPaymentDate)); // Dùng hàm format
        }
        // Cập nhật Tần suất
        if (tvFrequency != null) {
            String freq = "N/A";
            if (data.paymentFrequency != null) {
                // Map từ tiếng Anh sang tiếng Việt cho thân thiện
                if (data.paymentFrequency.equalsIgnoreCase("monthly")) freq = "Hàng tháng";
                else if (data.paymentFrequency.equalsIgnoreCase("weekly")) freq = "Hàng tuần";
                else if (data.paymentFrequency.equalsIgnoreCase("biweekly")) freq = "2 tuần/lần";
                else freq = data.paymentFrequency;
            }
            tvFrequency.setText(freq);
        }

        // Tính toán Dư nợ & Progress Bar
        if (data.totalLoanAmount != null && data.remainingBalance != null) {
            double total = data.totalLoanAmount;
            double remaining = data.remainingBalance;

            // Số tiền đã trả = Tổng vay - Còn lại
            double paid = total - remaining;
            if (paid < 0) paid = 0; // Đề phòng dữ liệu sai

            // Tính % tiến độ (Tránh chia cho 0)
            int progress = (total > 0) ? (int) ((paid / total) * 100) : 0;

            // Hiển thị lên UI
            if (tvRemainingBalance != null) {
                // Có thể hiển thị text hoặc chỉ số tiền
                tvRemainingBalance.setText("Dư nợ còn lại: " + formatCurrency(remaining));
            }

            if (tvPaidAmount != null) {
                tvPaidAmount.setText("Đã trả: " + formatCompactCurrency(paid));
            }

            if (tvTotalLoan != null) {
                tvTotalLoan.setText("Tổng: " + formatCompactCurrency(total));
            }

            if (pbLoanProgress != null) {
                pbLoanProgress.setProgress(progress);
            }
        }
    }
    private String formatCompactCurrency(double amount) {
        if (amount >= 1_000_000_000) {
            return String.format(Locale.US, "%.1f tỷ", amount / 1_000_000_000);
        } else if (amount >= 1_000_000) {
            return String.format(Locale.US, "%.1f tr", amount / 1_000_000);
        } else {
            return formatCurrency(amount);
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

    private String formatCurrency(Double amount) {
        if (amount == null) return "0 VND";
        return NumberFormat.getInstance(new Locale("vi", "VN")).format(amount) + " VND";
    }

    private String formatAccountNumber(String accNum) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < accNum.length(); i++) {
            if (i > 0 && i % 4 == 0) sb.append(" ");
            sb.append(accNum.charAt(i));
        }
        return sb.toString();
    }
}