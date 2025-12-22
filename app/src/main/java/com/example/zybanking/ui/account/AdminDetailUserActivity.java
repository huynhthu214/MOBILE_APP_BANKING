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
        if (userId == null || userId.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy người dùng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        loadUserDetail(userId);
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
            intent.putExtra("USER_ID", userId);
            startActivity(intent);
        });
    }

    private void loadToken() {
        SharedPreferences pref = getSharedPreferences("auth", Context.MODE_PRIVATE);
        token = "Bearer " + pref.getString("access_token", "");
    }

    private void loadUserDetail(String id) {
        apiService.getUserDetail(token, id).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(AdminDetailUserActivity.this, "Không lấy được dữ liệu", Toast.LENGTH_SHORT).show();
                    finish(); // Đóng activity nếu lỗi server
                    return;
                }

                UserResponse.Data data = response.body().getData();
                if (data == null || data.getUser() == null) {
                    Toast.makeText(AdminDetailUserActivity.this, "Dữ liệu người dùng trống", Toast.LENGTH_SHORT).show();
                    return;
                }

                // --- XÓA ĐOẠN CHECK ROLE SAI LOGIC NÀY ĐI ---
            /* if (!"admin".equalsIgnoreCase(data.getUser().getRole())) {
                 ...
            }
            */

                // Hiển thị thông tin
                bindUserInfo(data);
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(AdminDetailUserActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                // Không nhất thiết phải finish() ở đây, có thể cho user thử lại
            }
        });
    }

    private void bindUserInfo(UserResponse.Data data) {
        if (data == null) return;

        // 1. Map User Info
        User user = data.getUser(); // Sử dụng class User đã tách ra ở bước 1
        if (user != null) {
            tvName.setText(user.getFullName());
            tvEmail.setText(user.getEmail());
            tvPhone.setText(user.getPhone() != null ? user.getPhone() : "Chưa cập nhật");
            tvDate.setText(user.getCreatedAt());

            // Status
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

        // 2. Map EKYC
        UserResponse.Ekyc ekyc = data.getEkyc();
        if (ekyc != null) {
            tvStatusVerify.setText(ekyc.getStatus());
        } else {
            tvStatusVerify.setText("Chưa xác thực");
        }

        // 3. Map Accounts (Sử dụng getMapValue để an toàn)
        accountList.clear();
        double totalBalance = 0;

        if (data.getAccounts() != null) {
            for (Map<String, Object> accMap : data.getAccounts()) {
                Account acc = new Account();

                // Lấy dữ liệu an toàn
                acc.setAccountNumber(getMapValue(accMap, "ACCOUNT_NUMBER"));
                acc.setAccountType(getMapValue(accMap, "ACCOUNT_TYPE"));
                if (user != null) acc.setOwnerName(user.getFullName());

                // Xử lý Balance
                String balStr = getMapValue(accMap, "BALANCE");
                double balance = 0;
                try {
                    if (!balStr.isEmpty()) balance = Double.parseDouble(balStr);
                } catch (Exception e) {}
                acc.setBalance(balance);

                // Tính tổng tiền (Trừ khoản vay)
                if (!"mortgage".equalsIgnoreCase(acc.getAccountType())) {
                    totalBalance += balance;
                }

                // Lấy ID để click
                // String accId = getMapValue(accMap, "ACCOUNT_ID");
                // acc.setAccountId(accId); // Cần setter này trong Account.java

                accountList.add(acc);
            }
        }

        accountAdapter.notifyDataSetChanged();
        tvTotalBalance.setText(formatCurrency(totalBalance));
    }
    private String getMapValue(Map<String, Object> map, String key) {
        if (map == null) return "";
        if (map.containsKey(key) && map.get(key) != null) {
            return String.valueOf(map.get(key));
        }
        // Fallback: thử tìm key chữ thường (phòng hờ)
        if (map.containsKey(key.toLowerCase()) && map.get(key.toLowerCase()) != null) {
            return String.valueOf(map.get(key.toLowerCase()));
        }
        return "";
    }
    private String formatCurrency(double amount) {
        return NumberFormat.getCurrencyInstance(new Locale("vi", "VN")).format(amount);
    }
}
