package com.example.zybanking.ui.dashboard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zybanking.NavbarActivity;
import com.example.zybanking.R;
import com.example.zybanking.data.models.AccountSummaryResponse;
import com.example.zybanking.data.models.Transaction;
import com.example.zybanking.data.adapter.TransactionAdapter;
import com.example.zybanking.data.models.UserResponse;
import com.example.zybanking.data.remote.ApiService;
import com.example.zybanking.data.remote.RetrofitClient;
import com.example.zybanking.ui.ekyc.EKYCActivity;
import com.example.zybanking.ui.transaction.DepositActivity;
import com.example.zybanking.ui.map.MapActivity;
import com.example.zybanking.ui.transaction.ElectricWaterPayment;
import com.example.zybanking.ui.transaction.MortgageActivity;
import com.example.zybanking.ui.transaction.PhonePayment;
import com.example.zybanking.ui.transaction.SavingsActivity;
import com.example.zybanking.ui.transaction.WithdrawActivity;
import com.example.zybanking.ui.utilities.UtilitiesActivity;

public class HomeActivity extends NavbarActivity {
    private TextView tvUserName, tvBalance;
    private TextView tvSavingBalance, tvSavingRate, tvAccountNumber;
    private TextView tvMortgageAmount, tvNextPayment;
    private Button btnDeposit, btnWithdraw;
    private CardView cardSavings, cardMortgage, cardLocation, cardEKYC;
    private RecyclerView rvTransactions;
    private TextView btnViewMoreUtils;
    private LinearLayout btnElectricity, btnWater, btnPhone, btnTickets;
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
        // Header
        tvUserName = findViewById(R.id.tv_user_name);
        tvBalance = findViewById(R.id.tv_balance);
        tvAccountNumber = findViewById(R.id.tv_account_number);
        tvSavingBalance = findViewById(R.id.tv_saving_balance);
        tvSavingBalance = findViewById(R.id.tv_saving_balance);
        tvMortgageAmount = findViewById(R.id.tv_mortgage_amount);
        // Buttons Nạp/Rút (Cần thêm ID vào basic_home.xml nếu chưa có)
        btnDeposit = findViewById(R.id.btn_deposit);
        btnWithdraw = findViewById(R.id.btn_withdraw);
        // Nút "Xem thêm" ở mục Thao tác nhanh
        btnViewMoreUtils = findViewById(R.id.tv_view_more);
        // Quick Actions
        btnElectricity = findViewById(R.id.btn_electricity);
        btnWater = findViewById(R.id.btn_water);
        btnPhone = findViewById(R.id.btn_phone);
        btnTickets = findViewById(R.id.btn_tickets);
        // Cards (Tiết kiệm, Vay, Map)
        cardSavings = findViewById(R.id.card_savings);
        cardMortgage = findViewById(R.id.card_mortgage);
        cardLocation = findViewById(R.id.card_location);
        cardEKYC = findViewById(R.id.card_ekyc);
        // RecyclerView Giao dịch
        rvTransactions = findViewById(R.id.rv_transactions);
    }
    private void setupNavigation() {
        // 1. Nạp tiền
        if(btnDeposit != null) {
            btnDeposit.setOnClickListener(v -> startActivity(new Intent(this, DepositActivity.class)));
        }

        // 2. Rút tiền
        if(btnWithdraw != null) {
            btnWithdraw.setOnClickListener(v -> startActivity(new Intent(this, WithdrawActivity.class)));
        }

        // 3. Xem thêm tiện ích (Basic Utilities)
        View.OnClickListener utilListener = v -> startActivity(new Intent(this, UtilitiesActivity.class));
        if (btnViewMoreUtils != null) btnViewMoreUtils.setOnClickListener(utilListener);
        // Gán luôn cho các icon điện nước để tiện
        if (btnElectricity != null) {
            btnElectricity.setOnClickListener(v -> startActivity(new Intent(this, ElectricWaterPayment.class)));
        }
        if (btnWater != null) {
            btnWater.setOnClickListener(v -> startActivity(new Intent(this, ElectricWaterPayment.class)));
        }
        if (btnPhone != null) {
            btnPhone.setOnClickListener(v -> startActivity(new Intent(this, PhonePayment.class)));
        }
        if (btnTickets != null) { // Renamed from btnInternet for consistency if that's what you want
            btnTickets.setOnClickListener(v -> startActivity(new Intent(this, UtilitiesActivity.class)));
        }
        // 4. Tài khoản tiết kiệm
        if(cardSavings != null) {
            cardSavings.setOnClickListener(v -> startActivity(new Intent(this, SavingsActivity.class)));
        }

        // 5. Khoản vay thế chấp
        if(cardMortgage != null) {
            cardMortgage.setOnClickListener(v -> startActivity(new Intent(this, MortgageActivity.class)));
        }

        // 6. Tìm ATM/Chi nhánh (Map)
        if(cardLocation != null) {
            cardLocation.setOnClickListener(v -> startActivity(new Intent(this, MapActivity.class)));
        }
        // 7. Xác thực danh tính
        if(cardEKYC != null) {
            cardEKYC.setOnClickListener(v -> startActivity(new Intent(this, EKYCActivity.class)));
        }

    }

    private void loadUserData() {
        SharedPreferences pref = getSharedPreferences("auth", MODE_PRIVATE);
        String token = pref.getString("access_token", "");
        String userId = pref.getString("user_id", "");

        if(token.isEmpty()) return;

        ApiService api = RetrofitClient.getClient().create(ApiService.class);

        // 1. Lấy thông tin User để biết User có những tài khoản nào (List Accounts)
        api.getCurrentUser("Bearer " + token).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if(response.isSuccessful() && response.body() != null) {
                    UserResponse.User user = response.body().getData().getUser();
                    tvUserName.setText(user.getFullName());

                    // Lấy danh sách các tài khoản
                    List<Map<String, Object>> accounts = response.body().getData().getAccounts();

                    if (accounts != null) {
                        // 2. Duyệt qua từng tài khoản để lấy chi tiết
                        for (Map<String, Object> acc : accounts) {
                            String accId = (String) acc.get("ACCOUNT_ID");
                            fetchAccountDetail(accId); // Gọi hàm lấy chi tiết
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {}
        });
    }
    // Hàm gọi API /api/v1/accounts/{id}/summary
    private void fetchAccountDetail(String accountId) {
        ApiService api = RetrofitClient.getClient().create(ApiService.class);
        api.getAccountSummary(accountId).enqueue(new Callback<AccountSummaryResponse>() {
            @Override
            public void onResponse(Call<AccountSummaryResponse> call, Response<AccountSummaryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AccountSummaryResponse data = response.body();
                    updateUI(data); // Cập nhật giao diện dựa trên loại tài khoản
                }
            }

            @Override
            public void onFailure(Call<AccountSummaryResponse> call, Throwable t) {
                Log.e("HomeActivity", "Load summary failed: " + t.getMessage());
            }
        });
    }
    private void updateUI(AccountSummaryResponse data) {
        if (data.type == null) return;

        switch (data.type.toLowerCase()) {
            case "checking":
                if (tvBalance != null) {
                    tvBalance.setText(formatCurrency(data.balance));
                }

                // --- CẬP NHẬT SỐ TÀI KHOẢN ---
                if (tvAccountNumber != null && data.accountNumber != null) {
                    tvAccountNumber.setText(formatAccountNumber(data.accountNumber));
                }
                // -----------------------------

                if (data.lastTransactions != null && rvTransactions != null) {
                    TransactionAdapter adapter = new TransactionAdapter(data.lastTransactions);
                    rvTransactions.setAdapter(adapter);
                }
                break;

            case "saving":
                // 3. Cập nhật Card Tiết kiệm
                if (tvSavingBalance != null) {
                    tvSavingBalance.setText(formatCurrency(data.balance));
                }
                if (tvSavingRate != null && data.interestRate != null) {
                    tvSavingRate.setText("Lãi suất: " + (data.interestRate * 100) + "% / năm");
                }
                // Nếu muốn hiển thị lãi hàng tháng: data.monthlyInterest
                break;

            case "mortgage":
                // 4. Cập nhật Card Vay thế chấp
                if (tvMortgageAmount != null) {
                    tvMortgageAmount.setText(formatCurrency(data.remainingBalance));
                }
                break;
        }
    }
    private String formatAccountNumber(String accNum) {
        if (accNum == null) return "";
        // Nếu muốn che bớt số: return "**** " + accNum.substring(accNum.length() - 4);
        // Nếu muốn hiện hết và thêm khoảng trắng:
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < accNum.length(); i++) {
            if (i > 0 && i % 4 == 0) {
                sb.append(" ");
            }
            sb.append(accNum.charAt(i));
        }
        return sb.toString();
    }
    private String formatCurrency(double amount) {
        return NumberFormat.getInstance(new Locale("vi", "VN")).format(amount) + " VND";
    }

}