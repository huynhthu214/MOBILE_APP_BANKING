package com.example.zybanking.ui.account;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zybanking.HeaderAdmin;
import com.example.zybanking.R;
import com.example.zybanking.data.adapter.AdminAccountAdapter;
import com.example.zybanking.data.models.account.Account;

import java.util.ArrayList;
import java.util.List;

public class AdminDetailUserActivity extends HeaderAdmin {
    LinearLayout editInfo;
    private String userId;
    private RecyclerView rvAccounts;
    private AdminAccountAdapter accountAdapter;
    private List<Account> accountList = new ArrayList<>();
    private TextView tvName, tvPhone, tvEmail, tvDate, tvTotalBalance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_detail_users);
        initHeader();
        userId = getIntent().getStringExtra("USER_ID");
        if (userId != null) {
            loadUserDetail(userId);
        }
        tvName = findViewById(R.id.tv_detail_name);
        tvTotalBalance = findViewById(R.id.tv_detail_balance);
        tvPhone = findViewById(R.id.tv_detail_phone);
        tvEmail = findViewById(R.id.tv_detail_email);
        tvDate = findViewById(R.id.tv_detail_date);

        rvAccounts = findViewById(R.id.rv_user_accounts);
        rvAccounts.setLayoutManager(new LinearLayoutManager(this));
//        accountAdapter = new AdminAccountAdapter(this, accountList);
        rvAccounts.setAdapter(accountAdapter);

        String userId = getIntent().getStringExtra("USER_ID");
//        loadData(userId);

        editInfo = findViewById(R.id.edit_info);
        editInfo.setOnClickListener(v -> startActivity(new Intent(this, AdminEditInforActivity.class)));
    }

    private void loadUserDetail(String id) {
        // Gọi API: GET /api/v1/users/{id}
        // Hiển thị thông tin cá nhân và danh sách ACCOUNT của user này
    }

//    private void loadData(String userId) {
//        // Gọi API lấy chi tiết User và Accounts
//        apiService.getUserDetail(token, userId).enqueue(new Callback<UserDetailResponse>() {
//            @Override
//            public void onResponse(Call<UserDetailResponse> call, Response<UserDetailResponse> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    UserDetailData data = response.body().getData();
//
//                    // 1. Hiển thị thông tin Profile
//                    tvName.setText(data.getUser().getFullName());
//                    tvPhone.setText(data.getUser().getPhone());
//                    tvEmail.setText(data.getUser().getEmail());
//                    tvDate.setText(data.getUser().getCreatedAt());
//
//                    // 2. Cập nhật danh sách Account
//                    accountList.clear();
//                    accountList.addAll(data.getAccounts());
//                    accountAdapter.notifyDataSetChanged();
//
//                    // Tính tổng số dư hiển thị lên phần "Tổng tài sản"
//                    double total = 0;
//                    for (Account acc : accountList) total += acc.getBalance();
//                    tvTotalBalance.setText(formatMoney(total));
//                }
//            }
//
//            @Override
//            public void onFailure(Call<UserDetailResponse> call, Throwable t) {
//            }
//        });
//    }

}
