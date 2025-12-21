package com.example.zybanking.ui.dashboard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zybanking.NavbarActivity;
import com.example.zybanking.R;
import com.example.zybanking.data.adapter.TransactionAdapter;
import com.example.zybanking.data.models.AccountSummaryResponse;
import com.example.zybanking.data.models.Transaction;
import com.example.zybanking.data.models.TransactionHistoryItem;
import com.example.zybanking.data.models.UserResponse;
import com.example.zybanking.data.remote.ApiService;
import com.example.zybanking.data.remote.RetrofitClient;
import com.example.zybanking.ui.ekyc.EKYCActivity;
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
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends NavbarActivity {

    // Header
    private TextView tvUserName, tvBalance, tvAccountNumber;

    // Saving / Mortgage
    private TextView tvSavingBalance, tvSavingRate;
    private TextView tvMortgageAmount;

    // Buttons
    private Button btnDeposit, btnWithdraw;

    // Cards
    private CardView cardSavings, cardMortgage, cardLocation, cardEKYC;

    // Utilities
    private TextView btnViewMoreUtils;
    private LinearLayout btnElectricity, btnWater, btnPhone, btnTickets;

    // Transactions
    private RecyclerView rvTransactions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.basic_home);

        initViews();
        setupNavigation();
        initNavbar();
        loadUserData();
    }

    private void initViews() {
        tvUserName = findViewById(R.id.tv_user_name);
        tvBalance = findViewById(R.id.tv_balance);
        tvAccountNumber = findViewById(R.id.tv_account_number);

        tvSavingBalance = findViewById(R.id.tv_saving_balance);
        tvSavingRate = findViewById(R.id.tv_saving_rate);
        tvMortgageAmount = findViewById(R.id.tv_mortgage_amount);

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

        rvTransactions = findViewById(R.id.rv_transactions);
        rvTransactions.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupNavigation() {

        btnDeposit.setOnClickListener(v ->
                startActivity(new Intent(this, DepositActivity.class)));

        btnWithdraw.setOnClickListener(v ->
                startActivity(new Intent(this, WithdrawActivity.class)));

        btnViewMoreUtils.setOnClickListener(v ->
                startActivity(new Intent(this, UtilitiesActivity.class)));

        btnElectricity.setOnClickListener(v ->
                startActivity(new Intent(this, ElectricWaterPayment.class)));

        btnWater.setOnClickListener(v ->
                startActivity(new Intent(this, ElectricWaterPayment.class)));

        btnPhone.setOnClickListener(v ->
                startActivity(new Intent(this, PhonePayment.class)));

        btnTickets.setOnClickListener(v ->
                startActivity(new Intent(this, UtilitiesActivity.class)));

        cardSavings.setOnClickListener(v ->
                startActivity(new Intent(this, SavingsActivity.class)));

        cardMortgage.setOnClickListener(v ->
                startActivity(new Intent(this, MortgageActivity.class)));

        cardLocation.setOnClickListener(v ->
                startActivity(new Intent(this, MapActivity.class)));

        cardEKYC.setOnClickListener(v ->
                startActivity(new Intent(this, EKYCActivity.class)));
    }

    private void loadUserData() {
        SharedPreferences pref = getSharedPreferences("auth", MODE_PRIVATE);
        String token = pref.getString("access_token", "");

        if (token.isEmpty()) return;

        ApiService api = RetrofitClient.getClient().create(ApiService.class);

        api.getCurrentUser("Bearer " + token)
                .enqueue(new Callback<UserResponse>() {
                    @Override
                    public void onResponse(Call<UserResponse> call,
                                           Response<UserResponse> response) {

                        if (!response.isSuccessful() || response.body() == null) return;

                        UserResponse.User user =
                                response.body().getData().getUser();
                        tvUserName.setText(user.getFullName());

                        List<Map<String, Object>> accounts =
                                response.body().getData().getAccounts();

                        if (accounts == null) return;

                        for (Map<String, Object> acc : accounts) {

                            String type = (String) acc.get("TYPE");
                            String accId = (String) acc.get("ACCOUNT_ID");

                            if ("checking".equalsIgnoreCase(type)) {

                                SharedPreferences.Editor editor =
                                        getSharedPreferences("auth", MODE_PRIVATE).edit();

                                editor.putString("account_id", accId);
                                editor.apply();

                                Log.d("SESSION", "Saved checking account_id = " + accId);
                                Log.d("ACCOUNTS_RAW", acc.toString());

                                fetchAccountDetail(accId);
                                break;
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<UserResponse> call, Throwable t) {
                        Log.e("HomeActivity", t.getMessage());
                    }
                });
    }

    private void fetchAccountDetail(String accountId) {
        ApiService api = RetrofitClient.getClient().create(ApiService.class);

        api.getAccountSummary(accountId)
                .enqueue(new Callback<AccountSummaryResponse>() {
                    @Override
                    public void onResponse(Call<AccountSummaryResponse> call,
                                           Response<AccountSummaryResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            updateUI(response.body());
                        }
                    }

                    @Override
                    public void onFailure(Call<AccountSummaryResponse> call, Throwable t) {
                        Log.e("HomeActivity", t.getMessage());
                    }
                });
    }

    private void updateUI(AccountSummaryResponse data) {

        if (data.type == null) return;

        switch (data.type.toLowerCase()) {

            case "checking":
                tvBalance.setText(formatCurrency(data.balance));

                if (data.accountNumber != null) {
                    tvAccountNumber.setText(
                            formatAccountNumber(data.accountNumber)
                    );
                }

                if (data.lastTransactions != null) {
                    List<TransactionHistoryItem> items =
                            mapTransactions(data.lastTransactions);

                    rvTransactions.setAdapter(
                            new TransactionAdapter(this, items)
                    );
                }
                break;

            case "saving":
                tvSavingBalance.setText(formatCurrency(data.balance));

                if (data.interestRate != null) {
                    tvSavingRate.setText(
                            "Lãi suất: " + (data.interestRate * 100) + "% / năm"
                    );
                }
                break;

            case "mortgage":
                tvMortgageAmount.setText(
                        formatCurrency(data.remainingBalance)
                );
                break;
        }
    }

    private List<TransactionHistoryItem> mapTransactions(List<Transaction> list) {
        List<TransactionHistoryItem> result = new ArrayList<>();

        for (Transaction t : list) {

            boolean isIncome =
                    "deposit".equalsIgnoreCase(t.getType()) ||
                            "receive".equalsIgnoreCase(t.getType());

            String title = isIncome ? "Nhận tiền" : "Chi tiêu";

            result.add(new TransactionHistoryItem(
                    title,
                    t.getDate(),
                    t.getAmount(),
                    isIncome
            ));
        }
        return result;
    }

    private String formatAccountNumber(String accNum) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < accNum.length(); i++) {
            if (i > 0 && i % 4 == 0) sb.append(" ");
            sb.append(accNum.charAt(i));
        }
        return sb.toString();
    }

    private String formatCurrency(double amount) {
        return NumberFormat
                .getInstance(new Locale("vi", "VN"))
                .format(amount) + " VND";
    }
}
