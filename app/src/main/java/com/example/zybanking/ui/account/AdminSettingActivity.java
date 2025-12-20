package com.example.zybanking.ui.account;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.cardview.widget.CardView;

import com.example.zybanking.HeaderAdmin;
import com.example.zybanking.R;
import com.example.zybanking.data.models.UserResponse;
import com.example.zybanking.data.remote.ApiService;
import com.example.zybanking.data.remote.RetrofitClient;
import com.example.zybanking.ui.auth.LoginActivity;
import com.example.zybanking.ui.ekyc.VerifyEkycActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminSettingActivity extends HeaderAdmin {
    private TextView tvAdminName, tvAdminEmail, itemManageUsers;
    private LinearLayout itemVerifyEkyc;
    private CardView cardLogout;
    private ApiService apiService;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_setting);
        initHeader();
        initViews();
        setupData();
        loadAdminProfile();
        setupEvents();
    }

    private void initViews() {
        tvAdminName = findViewById(R.id.tv_admin_name);
        tvAdminEmail = findViewById(R.id.tv_admin_email);
        itemManageUsers = findViewById(R.id.item_manage_users);
        itemVerifyEkyc = findViewById(R.id.item_verify_ekyc);
        cardLogout = findViewById(R.id.card_logout);
    }

    private void setupData() {
        apiService = RetrofitClient.getClient().create(ApiService.class);
        SharedPreferences pref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String savedToken = pref.getString("auth_token", "");
        token = savedToken.startsWith("Bearer ") ? savedToken : "Bearer " + savedToken;
    }

    private void loadAdminProfile() {
        apiService.getCurrentUser(token).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserResponse.User admin = response.body().getData().getUser();
                    tvAdminName.setText(admin.getFullName());
                    tvAdminEmail.setText(admin.getEmail());
                }
            }
            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(AdminSettingActivity.this, "Lỗi kết nối server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupEvents() {
        // Chuyển đến màn hình duyệt eKYC
        itemVerifyEkyc.setOnClickListener(v -> {
            startActivity(new Intent(this, VerifyEkycActivity.class));
        });

        // Đăng xuất
        cardLogout.setOnClickListener(v -> {
            getSharedPreferences("UserPrefs", Context.MODE_PRIVATE).edit().clear().apply();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        // Bạn có thể thêm itemManageUsers.setOnClickListener để quản lý khách hàng
    }
}