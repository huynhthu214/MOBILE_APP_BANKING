package com.example.zybanking.ui.transaction;

import android.content.Intent;
import android.content.SharedPreferences;
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
import java.util.Map;

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
    private ApiService apiService;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.basic_savings);

        // BƯỚC 1: Ưu tiên lấy ID tài khoản tiết kiệm từ Intent (HomeActivity gửi sang)
        accountId = getIntent().getStringExtra("ACCOUNT_ID");

        // BƯỚC 2: Nếu Intent không có, mới tìm trong SharedPreferences
        if (accountId == null || accountId.isEmpty()) {
            SharedPreferences pref = getSharedPreferences("auth", MODE_PRIVATE);
            token = pref.getString("access_token", "");
            // Lưu ý: Đảm bảo ở HomeActivity bạn đã lưu ID này với key "saving_account_id"
            accountId = pref.getString("saving_account_id", "");
        }

        initViews();

        // BƯỚC 3: Kiểm tra và load dữ liệu
        if (accountId != null && !accountId.isEmpty()) {
            Log.d("SavingsActivity", "Loading data for Saving Account ID: " + accountId);
            loadData();
        } else {
            Toast.makeText(this, "Không xác định được tài khoản tiết kiệm", Toast.LENGTH_SHORT).show();
            // Có thể kết thúc Activity nếu không có ID
            // finish();
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

        double annualRate = (actualData.interestRate != null) ? actualData.interestRate : 0.045; // Mặc định 4.5% nếu null
        tvRate.setText((annualRate) + "% / Năm");

        // --- PHẦN THÊM MỚI ĐỂ HIỂN THỊ LÃI TẠM TÍNH ---
        if (tvProfit != null) {
            if (actualData.monthlyInterest != null && actualData.monthlyInterest > 0) {
                // Ưu tiên dùng dữ liệu tính toán từ Server trả về
                tvProfit.setText("+" + formatCurrency(actualData.monthlyInterest));
            } else {
                // Nếu server chưa có, dùng logic tạm tính tại Client để không bị hiện +0 VND
                double calculatedProfit = (actualData.balance * annualRate) * ((actualData.termMonths != null ? actualData.termMonths : 12) / 12.0);
                tvProfit.setText("+" + formatCurrency(calculatedProfit));
            }
        }
        // ----------------------------------------------

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

    // Hàm tính lãi dự tính (Giả sử trả lãi cuối kỳ)
    private double calculateExpectedInterest(double principal, double annualRate, String frequency) {
        double monthlyRate = annualRate / 100 / 12;
        if ("MONTHLY".equalsIgnoreCase(frequency)) {
            return principal * monthlyRate;
        } else if ("YEARLY".equalsIgnoreCase(frequency)) {
            return principal * (annualRate / 100);
        }
        return 0;
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
    private void loadInterestRates() {
        apiService.getInterestRates("Bearer " + token).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Giả sử server trả về Map chứa "SAVINGS_RATE"
                    Double rate = (Double) response.body().get("SAVINGS_RATE");
                    tvRate.setText(rate + "%/năm");

                    updateInterestPreview(rate);
                }
            }
            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {}
        });
    }
    // Logic để tính toán lại các con số khi lãi suất thay đổi
    private void updateInterestPreview(Double rate) {
        if (tvPrincipal != null && !tvPrincipal.getText().toString().isEmpty()) {
            try {
                // Lấy số dư hiện tại để tính toán lãi dự tính
                // Lưu ý: Bạn cần lọc bỏ chữ "VND" và dấu phân cách để parse số
                String principalStr = tvPrincipal.getText().toString()
                        .replaceAll("[^\\d]", "");
                double principal = Double.parseDouble(principalStr);

                // Tính lãi 1 năm
                double yearlyProfit = principal * (rate / 100);

                if (tvProfit != null) {
                    tvProfit.setText("+" + formatCurrency(yearlyProfit));
                }
            } catch (Exception e) {
                Log.e("SavingsActivity", "Lỗi tính toán xem trước lãi: " + e.getMessage());
            }
        }
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