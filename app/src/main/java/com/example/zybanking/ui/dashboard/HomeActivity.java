package com.example.zybanking.ui.dashboard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zybanking.NavbarActivity;
import com.example.zybanking.R;
import com.example.zybanking.data.adapter.TransactionAdapter;
import com.example.zybanking.data.models.account.AccountSummaryResponse;
import com.example.zybanking.data.models.auth.UserResponse;
import com.example.zybanking.data.models.transaction.Transaction;
import com.example.zybanking.data.remote.ApiService;
import com.example.zybanking.data.remote.RetrofitClient;
import com.example.zybanking.ui.ekyc.EkycActivity;
import com.example.zybanking.ui.map.MapActivity;
import com.example.zybanking.ui.transaction.DepositActivity;
import com.example.zybanking.ui.transaction.ElectricWaterPayment;
import com.example.zybanking.ui.transaction.MortgageActivity;
import com.example.zybanking.ui.transaction.PhonePayment;
import com.example.zybanking.ui.transaction.SavingsActivity;
import com.example.zybanking.ui.transaction.WithdrawActivity;
import com.example.zybanking.ui.utilities.UtilitiesActivity;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends NavbarActivity {
    private TextView tvUserName, tvBalance, tvSavingBalance, tvSavingRate, tvAccountNumber;
    private Button btnDeposit, btnWithdraw;
    private CardView cardSavings, cardMortgage, cardLocation, cardEKYC;
    private RecyclerView rvTransactions;
    private TextView btnViewMoreUtils, btnViewAll;
    private LinearLayout btnElectricity, btnWater, btnPhone, btnTickets;

    private View layoutSavingInfo, layoutMortgageInfo;
    private TextView tvNoSaving, tvNoMortgage;
    private TextView tvMortgagePaymentAmount, tvMortgageDueDate, tvMortgageRemaining;

    private ImageView imgToggleAccountNo;
    private boolean isAccountHidden = true;
    private String realAccountNumber = "";

    private String savingAccountId = null;
    private String mortgageAccountId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.basic_home);

        initViews();
        setupNavigation();
        loadUserData();
        initNavbar();
    }

    private void initViews() {
        tvUserName = findViewById(R.id.tv_user_name);
        tvBalance = findViewById(R.id.tv_balance);
        tvAccountNumber = findViewById(R.id.tv_account_number);

        tvSavingBalance = findViewById(R.id.tv_saving_balance);
        tvSavingRate = findViewById(R.id.tv_saving_rate);
        layoutSavingInfo = findViewById(R.id.layout_saving_info);
        tvNoSaving = findViewById(R.id.tv_no_saving);

        tvMortgagePaymentAmount = findViewById(R.id.tv_mortgage_payment_amount);
        tvMortgageDueDate = findViewById(R.id.tv_mortgage_due_date);
        tvMortgageRemaining = findViewById(R.id.tv_mortgage_remaining);
        layoutMortgageInfo = findViewById(R.id.layout_mortgage_info);
        tvNoMortgage = findViewById(R.id.tv_no_mortgage);

        btnDeposit = findViewById(R.id.btn_deposit);
        btnWithdraw = findViewById(R.id.btn_withdraw);
        btnViewMoreUtils = findViewById(R.id.tv_view_more);
        btnElectricity = findViewById(R.id.btn_electricity);
        btnWater = findViewById(R.id.btn_water);
        btnPhone = findViewById(R.id.btn_phone);
        btnTickets = findViewById(R.id.btn_tickets);

        cardSavings = findViewById(R.id.card_savings);
        cardMortgage = findViewById(R.id.card_mortgage);
        cardLocation = findViewById(R.id.card_location);
        cardEKYC = findViewById(R.id.card_ekyc);

        btnViewAll = findViewById(R.id.tv_view_all_transactions);
        rvTransactions = findViewById(R.id.rv_transactions);
        if (rvTransactions != null) {
            rvTransactions.setLayoutManager(new LinearLayoutManager(this));
        }
        imgToggleAccountNo = findViewById(R.id.img_toggle_account_no);
    }

    private void setupNavigation() {
        if (btnDeposit != null) btnDeposit.setOnClickListener(v -> startActivity(new Intent(this, DepositActivity.class)));
        if (btnWithdraw != null) btnWithdraw.setOnClickListener(v -> startActivity(new Intent(this, WithdrawActivity.class)));

        // Xử lý các nút Tiện ích
        if (btnElectricity != null) btnElectricity.setOnClickListener(v -> {
            Intent intent = new Intent(this, ElectricWaterPayment.class);
            intent.putExtra("SERVICE_TYPE", "ELECTRIC");
            startActivity(intent);
        });

        if (btnWater != null) btnWater.setOnClickListener(v -> {
            Intent intent = new Intent(this, ElectricWaterPayment.class);
            intent.putExtra("SERVICE_TYPE", "WATER");
            startActivity(intent);
        });

        if (btnPhone != null) btnPhone.setOnClickListener(v -> startActivity(new Intent(this, PhonePayment.class)));

        if (btnTickets != null || btnViewMoreUtils != null) {
            View.OnClickListener utilListener = v -> startActivity(new Intent(this, UtilitiesActivity.class));
            if (btnTickets != null) btnTickets.setOnClickListener(utilListener);
            if (btnViewMoreUtils != null) btnViewMoreUtils.setOnClickListener(utilListener);
        }

        if (btnViewAll != null) {
            btnViewAll.setOnClickListener(v -> startActivity(new Intent(this, HistoryActivity.class)));
        }

        // Click vào Card
        if (cardSavings != null) cardSavings.setOnClickListener(v -> {
            if (savingAccountId != null) {
                Intent intent = new Intent(this, SavingsActivity.class);
                intent.putExtra("ACCOUNT_ID", savingAccountId);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Bạn chưa có tài khoản tiết kiệm", Toast.LENGTH_SHORT).show();
            }
        });

        if (cardMortgage != null) cardMortgage.setOnClickListener(v -> {
            if (mortgageAccountId != null) {
                Intent intent = new Intent(this, MortgageActivity.class);
                intent.putExtra("ACCOUNT_ID", mortgageAccountId);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Bạn chưa có khoản vay", Toast.LENGTH_SHORT).show();
            }
        });

        if (imgToggleAccountNo != null) imgToggleAccountNo.setOnClickListener(v -> toggleAccountNumber());
        if (cardLocation != null) cardLocation.setOnClickListener(v -> startActivity(new Intent(this, MapActivity.class)));
        if (cardEKYC != null) cardEKYC.setOnClickListener(v -> startActivity(new Intent(this, EkycActivity.class)));
    }

    private void loadUserData() {
        SharedPreferences pref = getSharedPreferences("auth", MODE_PRIVATE);
        String token = pref.getString("access_token", "");
        if (token.isEmpty()) {
            Log.e("HomeActivity", "Token trống!");
            return;
        }

        ApiService api = RetrofitClient.getClient().create(ApiService.class);
        api.getCurrentUser("Bearer " + token).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserResponse.User user = response.body().getData().getUser();
                    if (tvUserName != null) tvUserName.setText(user.getFullName());

                    List<Map<String, Object>> accounts = response.body().getData().getAccounts();

                    if (accounts == null || accounts.isEmpty()) {
                        Log.e("HomeActivity", "User không có tài khoản nào!");
                        return;
                    }

                    // Log để kiểm tra dữ liệu về
                    Log.d("HomeActivity", "Số tài khoản tìm thấy: " + accounts.size());

                    for (Map<String, Object> acc : accounts) {
                        // Kiểm tra từng key có thể có
                        String accId = getSafeStringId(acc, "ACCOUNT_ID");
                        if (accId == null) accId = getSafeStringId(acc, "account_id");
                        if (accId == null) accId = getSafeStringId(acc, "SAVING_ACC_ID");
                        if (accId == null) accId = getSafeStringId(acc, "MORTAGE_ACC_ID");

                        if (accId != null) {
                            fetchAccountDetail(accId);
                        }
                    }
                } else {
                    Log.e("HomeActivity", "Lỗi lấy User: " + response.code());
                }
            }
            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Log.e("HomeActivity", "Lỗi mạng User: " + t.getMessage());
            }
        });
    }

    // Hàm an toàn để lấy ID và tránh lỗi "1.0"
    private String getSafeStringId(Map<String, Object> map, String key) {
        if (!map.containsKey(key) || map.get(key) == null) return null;
        Object val = map.get(key);
        // Nếu là số, chuyển về long để mất phần thập phân .0
        if (val instanceof Double) {
            return String.valueOf(((Double) val).longValue());
        }
        return String.valueOf(val);
    }

    private void fetchAccountDetail(String accountId) {
        Log.d("HomeActivity", "Đang gọi API chi tiết cho ID: " + accountId);

        ApiService api = RetrofitClient.getClient().create(ApiService.class);
        api.getAccountSummary(accountId).enqueue(new Callback<AccountSummaryResponse>() {
            @Override
            public void onResponse(Call<AccountSummaryResponse> call, Response<AccountSummaryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    updateUI(response.body(), accountId);
                } else {
                    Log.e("HomeActivity", "Lỗi lấy Account Detail (" + accountId + "): " + response.code());
                }
            }
            @Override
            public void onFailure(Call<AccountSummaryResponse> call, Throwable t) {
                Log.e("HomeActivity", "Fail API Detail: " + t.getMessage());
            }
        });
    }

    private void updateUI(AccountSummaryResponse response, String currentAccId) {
        // Kiểm tra null kỹ càng
        if (response == null || response.data == null) {
            Log.e("HomeActivity", "Response Data bị NULL. Kiểm tra lại Backend xem có trả về {data: ...} không?");
            return;
        }

        AccountSummaryResponse.AccountData actualData = response.data;

        // Kiểm tra type null
        if (actualData.type == null) {
            Log.e("HomeActivity", "Account Type bị NULL");
            return;
        }

        String type = actualData.type.toUpperCase().trim();
        Log.d("HomeActivity", "Cập nhật UI cho loại: " + type);

        switch (type) {
            case "CHECKING":
                // ... (Code xử lý Checking giữ nguyên như cũ)
                SharedPreferences.Editor editor = getSharedPreferences("auth", MODE_PRIVATE).edit();
                editor.putString("main_account_id", currentAccId);
                editor.apply();

                if (tvBalance != null) tvBalance.setText(formatCurrency(actualData.balance));

                // Cập nhật số tài khoản thật
                if (actualData.accountNumber != null) {
                    realAccountNumber = actualData.accountNumber;
                    updateAccountNumberDisplay();
                }

                if (actualData.lastTransactions != null && rvTransactions != null) {
                    List<Transaction> top3 = actualData.lastTransactions.size() > 3
                            ? actualData.lastTransactions.subList(0, 3)
                            : actualData.lastTransactions;
                    rvTransactions.setAdapter(new TransactionAdapter(this, top3));
                }
                break;

            case "SAVING":
                savingAccountId = currentAccId;
                if (tvNoSaving != null) tvNoSaving.setVisibility(View.GONE);
                if (layoutSavingInfo != null) layoutSavingInfo.setVisibility(View.VISIBLE);
                if (tvSavingBalance != null) tvSavingBalance.setText(formatCurrency(actualData.balance));
                // Kiểm tra null cho interestRate
                if (tvSavingRate != null) {
                    double rate = actualData.interestRate != null ? actualData.interestRate : 0.0;
                    tvSavingRate.setText("Lãi suất: " + (rate * 100) + "% / năm");
                }
                break;

            case "MORTGAGE":
                // ... (Code xử lý Mortgage giữ nguyên, thêm check null nếu cần) ...
                mortgageAccountId = currentAccId;
                if (tvNoMortgage != null) tvNoMortgage.setVisibility(View.GONE);
                if (layoutMortgageInfo != null) layoutMortgageInfo.setVisibility(View.VISIBLE);
                if (tvMortgagePaymentAmount != null) tvMortgagePaymentAmount.setText(formatCurrency(actualData.paymentAmount));
                if (tvMortgageRemaining != null) tvMortgageRemaining.setText(formatCurrency(actualData.remainingBalance));
                if (tvMortgageDueDate != null && actualData.nextPaymentDate != null) {
                    tvMortgageDueDate.setText("(Hạn: " + formatDate(actualData.nextPaymentDate) + ")");
                }
                break;
        }
    }

    // --- HELPER METHODS ---
    private void toggleAccountNumber() {
        isAccountHidden = !isAccountHidden;
        updateAccountNumberDisplay();
    }

    private void updateAccountNumberDisplay() {
        if (tvAccountNumber == null) return;
        if (isAccountHidden) {
            String masked = "**** **** **** " + (realAccountNumber.length() > 4 ? realAccountNumber.substring(realAccountNumber.length() - 4) : "****");
            tvAccountNumber.setText(masked);
            imgToggleAccountNo.setImageResource(R.drawable.ic_eye_off);
        } else {
            tvAccountNumber.setText(formatAccountNumber(realAccountNumber));
            imgToggleAccountNo.setImageResource(R.drawable.ic_eye_on);
        }
    }

    private String formatAccountNumber(String accNum) {
        if (accNum == null) return "";
        return accNum.replaceAll("(.{4})", "$1 ").trim();
    }

    private String formatCurrency(Double amount) {
        if (amount == null) return "0 VND";
        return NumberFormat.getInstance(new Locale("vi", "VN")).format(amount) + " VND";
    }

    private String formatDate(String dateString) {
        Date date = parseDateString(dateString);
        if (date != null) return new java.text.SimpleDateFormat("dd/MM/yyyy", new Locale("vi", "VN")).format(date);
        return dateString;
    }

    private Date parseDateString(String dateString) {
        if (dateString == null || dateString.isEmpty()) return null;
        String[] formats = {"EEE, dd MMM yyyy HH:mm:ss 'GMT'", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd"};
        for (String f : formats) {
            try {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(f, Locale.ENGLISH);
                if (f.contains("GMT")) sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT"));
                return sdf.parse(dateString);
            } catch (Exception ignored) {}
        }
        return null;
    }
}