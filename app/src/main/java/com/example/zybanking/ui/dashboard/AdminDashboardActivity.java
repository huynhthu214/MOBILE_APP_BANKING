package com.example.zybanking.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.cardview.widget.CardView;

import com.example.zybanking.HeaderAdmin;
import com.example.zybanking.R;
import com.example.zybanking.ui.account.AdminEditInforActivity;
import com.example.zybanking.ui.account.AdminRatesActivity;
import com.example.zybanking.ui.account.AdminSettingActivity;
import com.example.zybanking.ui.account.AdminUserActivity;
import com.example.zybanking.ui.account.CreateAccountActivity;
import com.example.zybanking.ui.ekyc.VerifyEkycActivity;
import com.example.zybanking.ui.transaction.AdminTransaction;

public class AdminDashboardActivity extends HeaderAdmin {

    private ImageButton btnAdmin;
    private LinearLayout btnCreateAccount;
    private LinearLayout btnEkyc;
    private LinearLayout btnRates;
    private LinearLayout btnSupport;
    private CardView cardUser, cardRevenue, cardTransac, cardTransacMoney, cardEKYC, cardCreate, cardRate, cardEdit;
    private TextView tvMore, tv_More;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_dashboard);

        btnAdmin = findViewById(R.id.btn_admin);

        if (btnAdmin != null) {
            btnAdmin.setOnClickListener(v -> {
                startActivity(new Intent(this, AdminSettingActivity.class));
            });
        }
        initDashboardButtons();
        initHeader();
    }
    private void initDashboardButtons() {
        btnCreateAccount = findViewById(R.id.btn_quick_create_account);
        btnEkyc = findViewById(R.id.btn_quick_ekyc);
        btnRates = findViewById(R.id.btn_quick_rates);
        btnSupport = findViewById(R.id.btn_quick_support);

        // 1. Nút Tạo Tài Khoản (Create Account)
        if (btnCreateAccount != null) {
            btnCreateAccount.setOnClickListener(v -> {
                startActivity(new Intent(this, CreateAccountActivity.class));
            });
        }

        // 2. Nút Duyệt eKYC
        if (btnEkyc != null) {
            btnEkyc.setOnClickListener(v -> {
                startActivity(new Intent(this, VerifyEkycActivity.class));
            });
        }

        // 3. Nút Quản lý Lãi suất (Rates)
        if (btnRates != null) {
            btnRates.setOnClickListener(v -> {
                startActivity(new Intent(this, AdminRatesActivity.class));
            });
        }

        // 4. Nút Hỗ trợ (Support)
        if (btnSupport != null) {
            btnSupport.setOnClickListener(v -> {
                // Ví dụ: startActivity(new Intent(this, CustomerSupportActivity.class));
            });
        }

        cardRate = findViewById(R.id.card_rate);
        cardCreate = findViewById(R.id.card_create_customer);
        cardEKYC = findViewById(R.id.card_ekyc);
        cardRevenue = findViewById(R.id.card_revenue);
        cardTransac = findViewById(R.id.card_transac);
        cardUser = findViewById(R.id.card_user);
        cardTransacMoney = findViewById(R.id.card_transac_money);
        cardEdit = findViewById(R.id.card_edit);

        if (cardRate != null) {
            cardRate.setOnClickListener(v -> {startActivity(new Intent(this, AdminRatesActivity.class));});
        }
        if (cardCreate != null) {
            cardCreate.setOnClickListener(v -> {startActivity(new Intent(this, CreateAccountActivity.class));});
        }
        if (cardEKYC != null) {
            cardEKYC.setOnClickListener(v -> {startActivity(new Intent(this, VerifyEkycActivity.class));});
        }
        if (cardRevenue != null) {
            cardRevenue.setOnClickListener(v -> {startActivity(new Intent(this, AdminReportActivity.class));});
        }
        if (cardTransac != null) {
            cardTransac.setOnClickListener(v -> {startActivity(new Intent(this, AdminTransaction.class));});
        }
        if (cardUser != null) {
            cardUser.setOnClickListener(v -> {startActivity(new Intent(this, AdminUserActivity.class));});
        }
        if (cardTransacMoney != null) {
            cardTransacMoney.setOnClickListener(v -> {startActivity(new Intent(this, AdminReportActivity.class));});
        }
        if (cardEdit != null) {
            cardEdit.setOnClickListener(v -> {startActivity(new Intent(this, AdminEditInforActivity.class));});
        }

        tv_More = findViewById(R.id.tv_more);
        tvMore = findViewById(R.id.tvMore);

        if (tv_More != null) {
            tv_More.setOnClickListener(v -> {startActivity(new Intent(this, AdminReportActivity.class));});
        }
        if (tvMore != null) {
            tvMore.setOnClickListener(v -> {startActivity(new Intent(this, AdminTransaction.class));});
        }
    }
}
