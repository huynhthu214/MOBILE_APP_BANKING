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
import com.example.zybanking.data.models.account.AccountSummaryResponse;
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
    private TextView tvPaymentAmount, tvDueDate, tvRemainingBalance, tvAccNo, tvInterestRate;
    private TextView tvFrequency, tvPaidAmount, tvTotalLoan;
    private ProgressBar pbLoanProgress;
    private Double currentPaymentAmount;
    private boolean isHidden = true;
    private String realAccNo = "";
    private String accountId;
    private Button btnPay;// Biến này lưu ID lấy từ Intent

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.basic_mortgage);
        // Lấy dữ liệu từ Intent truyền sang
        accountId = getIntent().getStringExtra("ACCOUNT_ID");
        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (accountId != null) {
            loadData();
        }
    }
    private void initViews() {
        btnBack = findViewById(R.id.btnBackMortgage);
        btnBack.setOnClickListener(v -> finish());

        // Ánh xạ View
        tvPaymentAmount = findViewById(R.id.tv_payment_amount);
        tvDueDate = findViewById(R.id.tv_mortgage_due_date);
        tvRemainingBalance = findViewById(R.id.tv_mortgage_remaining);

        tvAccNo = findViewById(R.id.tv_mortgage_acc_no);
        imgToggleAcc = findViewById(R.id.img_toggle_mortgage_acc);
        tvFrequency = findViewById(R.id.tv_frequency);
        tvPaidAmount = findViewById(R.id.tv_paid_amount);
        tvTotalLoan = findViewById(R.id.tv_total_loan);
        pbLoanProgress = findViewById(R.id.pb_loan_progress);
        tvInterestRate = findViewById(R.id.tv_mortgage_rate);

        if (imgToggleAcc != null) {
            imgToggleAcc.setOnClickListener(v -> {
                isHidden = !isHidden;
                updateAccNoDisplay();
            });
        }

        btnPay = findViewById(R.id.btn_pay_mortgage); // Ánh xạ nút
        btnPay.setOnClickListener(v -> {
            Intent intent = new Intent(MortgageActivity.this, MortgagePaymentActivity.class);
            intent.putExtra("ACCOUNT_ID", accountId);
            startActivity(intent);
        });
    }

    private void loadData() {
        // SỬA: Dùng đúng tên biến accountId đã khai báo ở trên
        if (accountId == null) return;

        ApiService api = RetrofitClient.getClient().create(ApiService.class);
        api.getAccountSummary(accountId).enqueue(new Callback<AccountSummaryResponse>() {
            @Override
            public void onResponse(Call<AccountSummaryResponse> call, Response<AccountSummaryResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                    // SỬA: Gọi hàm updateUI để cập nhật giao diện
                    // Không tự set text thủ công ở đây để tránh lỗi logic
                    updateUI(response.body());
                }
            }

            @Override
            public void onFailure(Call<AccountSummaryResponse> call, Throwable t) {
                Log.e("MortgageActivity", "Error: " + t.getMessage());
            }
        });
    } // <-- Đã thêm dấu đóng ngoặc hàm loadData (Lỗi của bạn nằm ở việc thiếu dấu này)

    private void updateUI(AccountSummaryResponse response) {
        if (response == null || response.data == null) {
            return;
        }
        AccountSummaryResponse.AccountData actualData = response.data;
        this.currentPaymentAmount = actualData.paymentAmount;
        // 1. Số tài khoản
        if (actualData.accountNumber != null) {
            realAccNo = actualData.accountNumber;
            updateAccNoDisplay();
        }

        // 2. Số tiền thanh toán kx`ỳ này
        if (tvPaymentAmount != null) {
            tvPaymentAmount.setText(formatCurrency(actualData.paymentAmount));
        }

        // 3. Ngày đến hạn
        if (tvDueDate != null && actualData.nextPaymentDate != null) {
            tvDueDate.setText(formatDate(actualData.nextPaymentDate));
        }

        // 4. Lãi suất
        if (tvInterestRate != null) {
            if (actualData.interestRate != null) {
                tvInterestRate.setText(actualData.interestRate + "%/năm");
            } else {
                tvInterestRate.setText("---");
            }
        }

        // 5. Tần suất
        if (tvFrequency != null) {
            String freq = "N/A";
            if (actualData.paymentFrequency != null) {
                if (actualData.paymentFrequency.equalsIgnoreCase("monthly")) freq = "Hàng tháng";
                else if (actualData.paymentFrequency.equalsIgnoreCase("weekly")) freq = "Hàng tuần";
                else if (actualData.paymentFrequency.equalsIgnoreCase("biweekly")) freq = "2 tuần/lần";
                else freq = actualData.paymentFrequency;
            }
            tvFrequency.setText(freq);
        }

        // 6. Tính toán Dư nợ & Progress Bar
        if (actualData.totalLoanAmount != null && actualData.remainingBalance != null) {
            double total = actualData.totalLoanAmount;
            double remaining = actualData.remainingBalance;

            double paid = total - remaining;
            if (paid < 0) paid = 0;

            int progress = (total > 0) ? (int) ((paid / total) * 100) : 0;

            if (tvRemainingBalance != null) {
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

        if (actualData.nextPaymentDate != null) {
            checkPaymentStatus(actualData.nextPaymentDate);
        }
    }

    private void checkPaymentStatus(String nextPaymentDateStr) {
        Date nextDate = parseDateString(nextPaymentDateStr);
        if (nextDate == null) return;

        Date now = new Date();
        // Xóa phần giờ phút giây để so sánh ngày chính xác
        now.setHours(0); now.setMinutes(0); now.setSeconds(0);

        long diffInMillis = nextDate.getTime() - now.getTime();
        long daysDiff = java.util.concurrent.TimeUnit.DAYS.convert(diffInMillis, java.util.concurrent.TimeUnit.MILLISECONDS);

        // TRƯỜNG HỢP 1: QUÁ HẠN (Ngày hạn nhỏ hơn ngày hiện tại)
        if (diffInMillis < 0) {
            btnPay.setEnabled(true);
            btnPay.setText("Thanh toán ngay (Quá hạn)");
            btnPay.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.RED)); // Nút màu đỏ

            tvDueDate.setText(formatDate(nextPaymentDateStr) + " (Quá hạn)");
            tvDueDate.setTextColor(android.graphics.Color.RED); // Chữ màu đỏ

            // Vẫn hiện số tiền cần đóng
            if (tvPaymentAmount != null && currentPaymentAmount != null) {
                tvPaymentAmount.setText(formatCurrency(currentPaymentAmount));
            }
        }
        // TRƯỜNG HỢP 2: ĐÃ THANH TOÁN (Ngày hạn còn xa, ví dụ > 20 ngày nữa mới tới)
        else if (daysDiff > 20) {
            btnPay.setEnabled(false);
            btnPay.setText("Đã thanh toán kỳ này");
            btnPay.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.GRAY));

            tvDueDate.setText(formatDate(nextPaymentDateStr));
            tvDueDate.setTextColor(android.graphics.Color.parseColor("#10B981")); // Màu xanh lá

            // Cập nhật payment amount hiển thị về 0
            if (tvPaymentAmount != null) {
                tvPaymentAmount.setText("0 VND");
            }
        }
        // TRƯỜNG HỢP 3: ĐẾN KỲ THANH TOÁN (Sắp đến hạn hoặc đang trong kỳ)
        else {
            btnPay.setEnabled(true);
            btnPay.setText("Thanh toán kỳ này");
            btnPay.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#0b5394")));

            tvDueDate.setText(formatDate(nextPaymentDateStr));
            tvDueDate.setTextColor(android.graphics.Color.parseColor("#111827")); // Màu đen
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
        if (accNum == null) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < accNum.length(); i++) {
            if (i > 0 && i % 4 == 0) sb.append(" ");
            sb.append(accNum.charAt(i));
        }
        return sb.toString();
    }
}