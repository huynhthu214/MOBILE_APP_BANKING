package com.example.zybanking.ui.dashboard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminDashboardActivity extends HeaderAdmin {

    private ImageButton btnAdmin;
    private LinearLayout btnCreateAccount, btnEkyc, btnRates, btnSupport;
    private CardView cardUser, cardRevenue, cardTransac, cardTransacMoney, cardEKYC, cardCreate, cardRate, cardEdit;
    private TextView tvMore, tv_More;

    // Các TextView hiển thị số liệu thực tế (Đã đồng bộ ID với XML)
    private TextView tvTotalUsersVal, tvTotalRevenueVal, tvTotalTransacCountVal, tvTotalTransacMoneyVal;

    private ApiService apiService;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_dashboard);

        apiService = RetrofitClient.getClient().create(ApiService.class);
        loadToken();

        initViews();           // Khởi tạo các View hiển thị số liệu
        initDashboardButtons(); // Khởi tạo nút bấm và sự kiện chuyển màn hình
        initHeader();

        loadStatistics();      // Tải dữ liệu từ backend
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
        tvTotalRevenueVal = findViewById(R.id.tv_total_revenue_val);
        tvTotalTransacCountVal = findViewById(R.id.tv_total_transac_count_val);
        tvTotalTransacMoneyVal = findViewById(R.id.tv_total_transac_money_val);
    }

    private void loadStatistics() {
        // Thực hiện gọi API thống kê từ Backend
        // Chú ý: Cần định nghĩa endpoint này trong ApiService (ví dụ: getAdminStats)
        /*
        apiService.getAdminStats(token).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> data = response.body();
                    tvTotalUsersVal.setText(String.valueOf(data.get("total_users")));
                    tvTotalRevenueVal.setText(data.get("total_revenue") + "đ");
                    tvTotalTransacCountVal.setText(String.valueOf(data.get("total_transactions")));
                    tvTotalTransacMoneyVal.setText(data.get("total_amount") + "đ");
                }
            }
            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(AdminDashboardActivity.this, "Không thể tải thống kê", Toast.LENGTH_SHORT).show();
            }
        });
        */
    }

    private void initDashboardButtons() {
        // 1. Ánh xạ các nút tác vụ nhanh
        btnCreateAccount = findViewById(R.id.btn_quick_create_account);
        btnEkyc = findViewById(R.id.btn_quick_ekyc);
        btnRates = findViewById(R.id.btn_quick_rates);
        btnSupport = findViewById(R.id.btn_quick_support);

        // 2. Ánh xạ các CardView chính
        cardRate = findViewById(R.id.card_rate);
        cardCreate = findViewById(R.id.card_create_customer);
        cardEKYC = findViewById(R.id.card_ekyc);
        cardRevenue = findViewById(R.id.card_revenue);
        cardTransac = findViewById(R.id.card_transac);
        cardUser = findViewById(R.id.card_user);
        cardTransacMoney = findViewById(R.id.card_transac_money);
        cardEdit = findViewById(R.id.card_edit);

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

        if (cardUser != null) cardUser.setOnClickListener(v -> startActivity(new Intent(this, AdminUserActivity.class)));
        if (cardTransac != null) cardTransac.setOnClickListener(v -> startActivity(new Intent(this, AdminTransaction.class)));
        if (cardEdit != null) cardEdit.setOnClickListener(v -> startActivity(new Intent(this, AdminEditInforActivity.class)));

        // Xem thêm / Báo cáo
        tv_More = findViewById(R.id.tv_more);
        tvMore = findViewById(R.id.tvMore);

        View.OnClickListener reportListener = v -> Toast.makeText(this, "Đang mở báo cáo...", Toast.LENGTH_SHORT).show();
        if (cardRevenue != null) cardRevenue.setOnClickListener(reportListener);
        if (cardTransacMoney != null) cardTransacMoney.setOnClickListener(reportListener);
        if (tv_More != null) tv_More.setOnClickListener(reportListener);
        if (tvMore != null) tvMore.setOnClickListener(v -> startActivity(new Intent(this, AdminTransaction.class)));
    }
}