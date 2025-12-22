package com.example.zybanking.ui.account;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.zybanking.HeaderAdmin;
import com.example.zybanking.R;
import com.example.zybanking.data.adapter.AdminAccountAdapter;
import com.example.zybanking.data.models.account.Account;
import com.example.zybanking.data.models.auth.UserResponse;
import com.example.zybanking.data.remote.ApiService;
import com.example.zybanking.data.remote.RetrofitClient;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.graphics.Color;
import java.util.Map;
public class AdminDetailUserActivity extends HeaderAdmin {
    private String userId, token;
    private RecyclerView rvAccounts;
    private AdminAccountAdapter accountAdapter;
    private List<Account> accountList = new ArrayList<>();
    private TextView tvName, tvPhone, tvEmail, tvDate, tvTotalBalance, tvStatusVerify, tvStatusActive;
    private ImageView btnEdit;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_detail_users);
        initHeader();
        loadToken();
        apiService = RetrofitClient.getClient().create(ApiService.class);
        userId = getIntent().getStringExtra("USER_ID");
        initViews();
        if (userId != null) {
            loadData(userId);
        }
    }
    private void initViews() {
        // Ánh xạ TextView
        tvName = findViewById(R.id.tv_detail_name);
        tvTotalBalance = findViewById(R.id.tv_detail_balance);
        tvPhone = findViewById(R.id.tv_detail_phone);
        tvEmail = findViewById(R.id.tv_detail_email);
        tvDate = findViewById(R.id.tv_detail_date);
        tvStatusVerify = findViewById(R.id.tv_status_verify);
        tvStatusActive = findViewById(R.id.tv_status_active);
        btnEdit = findViewById(R.id.btn_action_edit);

        // Ánh xạ RecyclerView
        rvAccounts = findViewById(R.id.rv_user_accounts);

        // THIẾT LẬP ADAPTER TẠI ĐÂY (Sau khi rvAccounts đã findViewById)
        rvAccounts.setLayoutManager(new LinearLayoutManager(this));
        accountAdapter = new AdminAccountAdapter(this, accountList);
        rvAccounts.setAdapter(accountAdapter);

        // Sự kiện nút sửa
        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminEditInforActivity.class);
            intent.putExtra("USER_ID", userId);
            startActivity(intent);
        });
    }

    private void loadToken() {
        SharedPreferences pref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        token = "Bearer " + pref.getString("auth_token", "");
    }

    private void loadData(String id) {
        apiService.getUserDetail(token, id).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserResponse.Data data = response.body().getData();
                    UserResponse.User user = data.getUser();
                    UserResponse.Ekyc ekyc = data.getEkyc();

                    tvName.setText(user.getFullName());
                    tvEmail.setText(user.getEmail());
                    tvPhone.setText(user.getPhone());
                    tvDate.setText(user.getCreatedAt());

                    if (ekyc != null) {
                        tvStatusVerify.setText(ekyc.getStatus());
                    } else {
                        tvStatusVerify.setText("Chưa xác thực");
                    }

                    boolean active = user.isActive();
                    tvStatusActive.setText(active ? "Hoạt động" : "Bị khóa");
                    // Sử dụng Color.parseColor hoặc Color.RED
                    tvStatusActive.setTextColor(active ? Color.parseColor("#16A34A") : Color.RED);

                    // SỬA LỖI ADDALL: Chuyển đổi Map sang Account
                    accountList.clear();
                    if (data.getAccounts() != null) {
                        for (Map<String, Object> accMap : data.getAccounts()) {
                            Account acc = new Account();
                            acc.setAccountNumber(String.valueOf(accMap.get("ACCOUNT_NUMBER")));
                            acc.setAccountType(String.valueOf(accMap.get("ACCOUNT_TYPE")));
                            if (accMap.get("BALANCE") != null) {
                                acc.setBalance(Double.parseDouble(accMap.get("BALANCE").toString()));
                            }
                            accountList.add(acc);
                        }
                    }
                    accountAdapter.notifyDataSetChanged();

                    // Tính tổng số dư
                    double total = 0;
                    for (Account acc : accountList) {
                        total += acc.getBalance();
                    }
                    tvTotalBalance.setText(formatCurrency(total));
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                // Sửa lỗi UserDetailResponse -> UserResponse
                Toast.makeText(AdminDetailUserActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String formatCurrency(double amount) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return formatter.format(amount);
    }
}