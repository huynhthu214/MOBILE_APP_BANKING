package com.example.zybanking.ui.account;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zybanking.HeaderAdmin;
import com.example.zybanking.R;
import com.example.zybanking.data.adapter.AdminAccountAdapter;
import com.example.zybanking.data.models.account.Account;
import com.example.zybanking.data.models.auth.User;
import com.example.zybanking.data.models.auth.UserResponse;
import com.example.zybanking.data.remote.ApiService;
import com.example.zybanking.data.remote.RetrofitClient;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminDetailUserActivity extends HeaderAdmin {

    private String userId, token;

    private TextView tvName, tvPhone, tvEmail, tvDate;
    private TextView tvTotalBalance, tvStatusVerify, tvStatusActive;
    private ImageView btnEdit;

    private RecyclerView rvAccounts;
    private AdminAccountAdapter accountAdapter;
    private final List<Account> accountList = new ArrayList<>();

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_detail_users);
        initHeader();
        loadToken();
        apiService = RetrofitClient.getClient().create(ApiService.class);

        userId = getIntent().getStringExtra("USER_ID");
        // Tạm thời bỏ qua check userId để test giao diện
        // if (userId == null || userId.isEmpty()) { ... }

        initViews();

        loadMockData();
    }

    private void initViews() {
        tvName = findViewById(R.id.tv_detail_name);
        tvPhone = findViewById(R.id.tv_detail_phone);
        tvEmail = findViewById(R.id.tv_detail_email);
        tvDate = findViewById(R.id.tv_detail_date);
        tvTotalBalance = findViewById(R.id.tv_total_balance_value);
        tvStatusVerify = findViewById(R.id.tv_status_verify);
        tvStatusActive = findViewById(R.id.tv_status_active);
        btnEdit = findViewById(R.id.btn_action_edit);

        rvAccounts = findViewById(R.id.rv_user_accounts);
        rvAccounts.setLayoutManager(new LinearLayoutManager(this));
        accountAdapter = new AdminAccountAdapter(this, accountList);
        rvAccounts.setAdapter(accountAdapter);

        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminEditInforActivity.class);
            intent.putExtra("USER_ID", userId != null ? userId : "U_008");
            startActivity(intent);
        });
    }

    // --- HÀM MOCK DATA ---
    private void loadMockData() {
        // 1. Giả lập thông tin User
        tvName.setText("Huynh Minh Thu");
        tvPhone.setText("0123456789");
        tvEmail.setText("minhthu@gmail.com");
        tvDate.setText("01/01/2004");

        // 2. Giả lập trạng thái
        tvStatusActive.setText("Hoạt động");
        tvStatusActive.setTextColor(Color.parseColor("#16A34A"));
        tvStatusActive.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#F0FDF4")));

        tvStatusVerify.setText("approved"); // Hoặc "pending", "rejected"

        // 3. Giả lập danh sách tài khoản
        accountList.clear();

        // Tài khoản 1: Thanh toán
        Account acc1 = new Account();
        acc1.setAccountNumber("1900123456789");
        acc1.setAccountType("checking"); // checking, saving, mortgage
        acc1.setBalance(5500000.0);
        acc1.setOwnerName("Huynh Minh Thu");
        accountList.add(acc1);

        // Tài khoản 2: Tiết kiệm
        Account acc2 = new Account();
        acc2.setAccountNumber("888899990000");
        acc2.setAccountType("saving");
        acc2.setBalance(120000000.0);
        acc2.setOwnerName("Nguyễn Văn A");
        accountList.add(acc2);

        // Tài khoản 3: Khoản vay
        Account acc3 = new Account();
        acc3.setAccountNumber("VN-LOAN-001");
        acc3.setAccountType("mortgage");
        acc3.setBalance(500000000.0); // Dư nợ
        acc3.setOwnerName("Nguyễn Văn A");
        accountList.add(acc3);

        // 4. Tính tổng tiền (trừ khoản vay)
        double totalBalance = 0;
        for (Account acc : accountList) {
            if (!"mortgage".equalsIgnoreCase(acc.getAccountType())) {
                totalBalance += acc.getBalance();
            }
        }

        // 5. Cập nhật UI
        accountAdapter.notifyDataSetChanged();
        tvTotalBalance.setText(formatCurrency(totalBalance));

    }

    private void loadToken() {
        SharedPreferences pref = getSharedPreferences("auth", Context.MODE_PRIVATE);
        token = "Bearer " + pref.getString("access_token", "");
    }

    // Giữ lại hàm API thật để sau này dùng
    private void loadUserDetail(String id) {
        if (id == null) return;
        apiService.getUserDetail(token, id).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserResponse.Data data = response.body().getData();
                    bindUserInfo(data);
                } else {
                    Toast.makeText(AdminDetailUserActivity.this, "Không lấy được dữ liệu", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(AdminDetailUserActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bindUserInfo(UserResponse.Data data) {
        if (data == null) return;
        User user = data.getUser();
        if (user != null) {
            tvName.setText(user.getFullName());
            tvEmail.setText(user.getEmail());
            tvPhone.setText(user.getPhone() != null ? user.getPhone() : "Chưa cập nhật");
            tvDate.setText(user.getCreatedAt());

            if (user.isActive()) {
                tvStatusActive.setText("Hoạt động");
                tvStatusActive.setTextColor(Color.parseColor("#16A34A"));
                tvStatusActive.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#F0FDF4")));
            } else {
                tvStatusActive.setText("Đã khóa");
                tvStatusActive.setTextColor(Color.RED);
                tvStatusActive.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#FEF2F2")));
            }
        }

        UserResponse.Ekyc ekyc = data.getEkyc();
        if (ekyc != null) {
            tvStatusVerify.setText(ekyc.getStatus());
        } else {
            tvStatusVerify.setText("Chưa xác thực");
        }

        accountList.clear();
        double totalBalance = 0;

        if (data.getAccounts() != null) {
            for (Map<String, Object> accMap : data.getAccounts()) {
                Account acc = new Account();
                acc.setAccountNumber(getMapValue(accMap, "ACCOUNT_NUMBER"));
                acc.setAccountType(getMapValue(accMap, "ACCOUNT_TYPE"));
                if (user != null) acc.setOwnerName(user.getFullName());

                String balStr = getMapValue(accMap, "BALANCE");
                double balance = 0;
                try {
                    if (!balStr.isEmpty()) balance = Double.parseDouble(balStr);
                } catch (Exception e) {}
                acc.setBalance(balance);

                if (!"mortgage".equalsIgnoreCase(acc.getAccountType())) {
                    totalBalance += balance;
                }
                accountList.add(acc);
            }
        }

        accountAdapter.notifyDataSetChanged();
        tvTotalBalance.setText(formatCurrency(totalBalance));
    }

    private String getMapValue(Map<String, Object> map, String key) {
        if (map == null) return "";
        if (map.containsKey(key) && map.get(key) != null) return String.valueOf(map.get(key));
        if (map.containsKey(key.toLowerCase()) && map.get(key.toLowerCase()) != null) return String.valueOf(map.get(key.toLowerCase()));
        return "";
    }

    private String formatCurrency(double amount) {
        return NumberFormat.getCurrencyInstance(new Locale("vi", "VN")).format(amount);
    }
}