package com.example.zybanking.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
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
        String token = getSharedPreferences("auth", MODE_PRIVATE)
                .getString("access_token", "");
        if(token.isEmpty()) return;

        ApiService api = RetrofitClient.getClient().create(ApiService.class);
        api.getCurrentUser("Bearer " + token).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if(response.isSuccessful() && response.body() != null) {
                    UserResponse.User user = response.body().getData().getUser();
                    tvUserName.setText(user.getFullName());

                    // lấy account đầu tiên để hiển thị balance
                    if(response.body().getData().getAccounts() != null && !response.body().getData().getAccounts().isEmpty()) {
                        String accountId = response.body().getData().getAccounts().get(0).get("ACCOUNT_ID").toString();
                        loadAccountSummary(accountId, token);
                    }
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {}
        });
    }

    private void loadAccountSummary(String accountId, String token) {
        ApiService api = RetrofitClient.getClient().create(ApiService.class);
        api.getAccountSummary(accountId).enqueue(new Callback<AccountSummaryResponse>() {
            @Override
            public void onResponse(Call<AccountSummaryResponse> call, Response<AccountSummaryResponse> response) {
                if(response.isSuccessful() && response.body() != null) {
                    tvBalance.setText(formatCurrency(response.body().getBalance()));

                    // load giao dịch gần đây
                    List<Transaction> txns = response.body().getLastTransactions();
                    TransactionAdapter adapter = new TransactionAdapter(txns);
                    rvTransactions.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<AccountSummaryResponse> call, Throwable t) {}
        });
    }

    private void loadRecentTransactions(String userId) {
        ApiService api = RetrofitClient.getClient().create(ApiService.class);
        api.getRecentTransactions(userId, 5).enqueue(new Callback<List<Transaction>>() {
            @Override
            public void onResponse(Call<List<Transaction>> call, Response<List<Transaction>> response) {
                if(response.isSuccessful() && response.body() != null) {
                    List<Transaction> txns = response.body();
                    TransactionAdapter adapter = new TransactionAdapter(txns);
                    rvTransactions.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<Transaction>> call, Throwable t) {
                // Toast báo lỗi nếu muốn
            }
        });
    }
    private String formatCurrency(double amount) {
        return NumberFormat.getInstance(new Locale("vi", "VN")).format(amount) + " VND";
    }

}