package com.example.zybanking.ui.dashboard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.cardview.widget.CardView;

import com.example.zybanking.HeaderAdmin;
import com.example.zybanking.R;
import com.example.zybanking.data.remote.ApiService;
import com.example.zybanking.data.remote.RetrofitClient;
import com.example.zybanking.ui.account.AdminEditInforActivity;
import com.example.zybanking.ui.account.AdminRatesActivity;
import com.example.zybanking.ui.account.AdminSettingActivity;
import com.example.zybanking.ui.account.AdminUserActivity;
import com.example.zybanking.ui.account.CreateAccountActivity;
import com.example.zybanking.ui.ekyc.VerifyEkycActivity;
import com.example.zybanking.ui.transaction.AdminTransaction;

import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminDashboardActivity extends HeaderAdmin {

    private ImageButton btnAdmin;
    private LinearLayout btnCreateAccount, btnEkyc, btnRates;
    private CardView cardEKYC, cardCreate, cardRate, cardEdit;
    private TextView tvMore;
    private LinearLayout layoutRecentTransactions;
    private TextView tvTotalUsersVal, tvTotalTransacCountVal, tvTotalTransacMoneyVal;

    private ApiService apiService;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_dashboard);

        apiService = RetrofitClient.getClient().create(ApiService.class);
        loadToken();

        initViews();
        initDashboardButtons();
        initHeader();

        loadStatistics();
    }

    private void loadToken() {
        SharedPreferences pref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String savedToken = pref.getString("auth_token", "");
        token = savedToken.startsWith("Bearer ") ? savedToken : "Bearer " + savedToken;
    }

    private void initViews() {
        btnAdmin = findViewById(R.id.btn_admin);

        // Ánh xạ các TextView hiển thị giá trị
        tvTotalUsersVal = findViewById(R.id.tv_total_users_val);
        tvTotalTransacCountVal = findViewById(R.id.tv_total_transac_count_val);
        tvTotalTransacMoneyVal = findViewById(R.id.tv_total_transac_money_val);
        layoutRecentTransactions = findViewById(R.id.layout_recent_transactions);
        tvMore = findViewById(R.id.tvMore);
        cardEdit = findViewById(R.id.card_edit);
    }

    private void loadStatistics() {
        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Chưa đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }

        apiService.getAdminStats(token).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> res) {
                if (res.isSuccessful() && res.body() != null) {
                    Map<String, Object> data = res.body();

                    // 1. Tổng người dùng
                    Object usersObj = data.get("total_users");
                    // Ép kiểu an toàn: Chuyển về String rồi hiển thị
                    tvTotalUsersVal.setText(String.valueOf(usersObj).replace(".0", ""));

                    // 2. Tổng số lượng giao dịch
                    Object transCountObj = data.get("total_transactions");
                    tvTotalTransacCountVal.setText(String.valueOf(transCountObj).replace(".0", ""));

                    // 3. Tổng tiền giao dịch (Format dạng tiền tệ: 1,000,000 đ)
                    Object amountObj = data.get("total_amount");
                    double amount = 0;
                    if (amountObj != null) {
                        amount = Double.parseDouble(amountObj.toString());
                    }

                    // Format số: ví dụ 1000000 -> 1,000,000
                    String formattedMoney = String.format("%,.0f đ", amount);
                    tvTotalTransacMoneyVal.setText(formattedMoney);

                    if (data.containsKey("recent_transactions")) {
                        List<Map<String, Object>> transactions = (List<Map<String, Object>>) data.get("recent_transactions");
                        renderRecentTransactions(transactions);
                    }
                } else {
                    Toast.makeText(AdminDashboardActivity.this, "Lỗi tải dữ liệu: " + res.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(AdminDashboardActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void renderRecentTransactions(List<Map<String, Object>> transactions) {
        layoutRecentTransactions.removeAllViews(); // Xóa dữ liệu cũ/mẫu

        if (transactions == null || transactions.isEmpty()) {
            TextView emptyView = new TextView(this);
            emptyView.setText("Chưa có giao dịch nào");
            emptyView.setGravity(Gravity.CENTER);
            emptyView.setPadding(0, 30, 0, 30);
            layoutRecentTransactions.addView(emptyView);
            return;
        }

        for (Map<String, Object> tx : transactions) {
            String txId = String.valueOf(tx.get("TRANSACTION_ID"));
            String rawDate = String.valueOf(tx.get("CREATED_AT"));

            double amt = 0;
            try {
                amt = Double.parseDouble(String.valueOf(tx.get("AMOUNT")));
            } catch (Exception e){}

            // Layout cha của 1 dòng
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.VERTICAL);
            // Padding này tạo khoảng cách thoáng giữa các dòng
            row.setPadding(0, 16, 0, 16);

            // Hàng 1: Mã + Tiền
            LinearLayout line1 = new LinearLayout(this);
            line1.setOrientation(LinearLayout.HORIZONTAL);

            TextView tvId = new TextView(this);
            tvId.setText("GD #" + txId);
            tvId.setTypeface(null, Typeface.BOLD);
            tvId.setTextColor(Color.parseColor("#333333"));
            tvId.setTextSize(14);

            // View tàng hình để đẩy tiền sang phải (Spacer)
            View spacer = new View(this);
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, 1, 1.0f);
            spacer.setLayoutParams(p);

            TextView tvAmt = new TextView(this);
            String sign = amt >= 0 ? "+" : "";
            tvAmt.setText(sign + String.format("%,.0f đ", amt));
            tvAmt.setTypeface(null, Typeface.BOLD);
            tvAmt.setTextColor(amt >= 0 ? Color.parseColor("#2E7D32") : Color.BLACK);
            tvAmt.setTextSize(14);

            line1.addView(tvId);
            line1.addView(spacer);
            line1.addView(tvAmt);

            // Hàng 2: Ngày giờ
            TextView tvDate = new TextView(this);
            tvDate.setText(rawDate);
            tvDate.setTextSize(11);
            tvDate.setTextColor(Color.GRAY);
            tvDate.setPadding(0, 4, 0, 0);

            row.addView(line1);
            row.addView(tvDate);

            // Thêm đường kẻ mờ phân cách
            View divider = new View(this);
            divider.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1));
            divider.setBackgroundColor(Color.parseColor("#EEEEEE"));

            layoutRecentTransactions.addView(row);
            layoutRecentTransactions.addView(divider);
        }
    }
    private void initDashboardButtons() {
        // 1. Ánh xạ các nút tác vụ nhanh
        btnCreateAccount = findViewById(R.id.btn_quick_create_account);
        btnEkyc = findViewById(R.id.btn_quick_ekyc);
        btnRates = findViewById(R.id.btn_quick_rates);

        // 2. Ánh xạ các CardView chính
        cardRate = findViewById(R.id.card_rate);
        cardCreate = findViewById(R.id.card_create_customer);
        cardEKYC = findViewById(R.id.card_ekyc);

        // 3. Gán sự kiện click (Tập trung)
        if (btnAdmin != null) btnAdmin.setOnClickListener(v -> startActivity(new Intent(this, AdminSettingActivity.class)));

        View.OnClickListener createAccListener = v -> startActivity(new Intent(this, CreateAccountActivity.class));
        if (btnCreateAccount != null) btnCreateAccount.setOnClickListener(createAccListener);
        if (cardCreate != null) cardCreate.setOnClickListener(createAccListener);

        View.OnClickListener ekycListener = v -> startActivity(new Intent(this, VerifyEkycActivity.class));
        if (btnEkyc != null) btnEkyc.setOnClickListener(ekycListener);
        if (cardEKYC != null) cardEKYC.setOnClickListener(ekycListener);

        View.OnClickListener rateListener = v -> startActivity(new Intent(this, AdminRatesActivity.class));
        if (btnRates != null) btnRates.setOnClickListener(rateListener);
        if (cardRate != null) cardRate.setOnClickListener(rateListener);
        if (cardEdit != null) cardEdit.setOnClickListener(v -> startActivity(new Intent(this, AdminUserActivity.class)));
        if (tvMore != null) {
            tvMore.setOnClickListener(v -> {
                Intent intent = new Intent(AdminDashboardActivity.this, AdminTransaction.class);
                startActivity(intent);
            });
        }
    }


}