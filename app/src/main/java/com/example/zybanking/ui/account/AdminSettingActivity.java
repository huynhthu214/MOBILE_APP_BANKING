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
import com.example.zybanking.data.models.auth.UserResponse;
import com.example.zybanking.data.remote.ApiService;
import com.example.zybanking.data.remote.RetrofitClient;
import com.example.zybanking.ui.auth.ChangePasswordActivity;
import com.example.zybanking.ui.auth.LoginActivity;
import com.example.zybanking.ui.ekyc.VerifyEkycActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminSettingActivity extends HeaderAdmin {
    private TextView tvAdminName, tvAdminEmail;
    private LinearLayout itemManageUsers, itemVerifyEkyc, itemAdjustRates, itemChangePassword;
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
        itemAdjustRates = findViewById(R.id.item_adjust_rates);
        itemChangePassword = findViewById(R.id.item_change_password);

        cardLogout = findViewById(R.id.card_logout);
    }

    private void setupData() {
        apiService = RetrofitClient.getClient().create(ApiService.class);

        SharedPreferences pref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String savedToken = pref.getString("auth_token", "");

        if (savedToken.isEmpty()) {
            android.util.Log.e("API_DEBUG", "Token bị rỗng! Admin chưa đăng nhập.");
            // Nếu không có token, nên chuyển về màn hình Login
            return;
        }

        // Xử lý chuẩn để tránh lỗi "Bearer Bearer"
        if (savedToken.startsWith("Bearer ")) {
            token = savedToken;
        } else {
            token = "Bearer " + savedToken;
        }

        android.util.Log.d("API_DEBUG", "Token gửi đi: " + token);
    }

    private void loadAdminProfile() {
        // 1. Lấy đúng file pref "auth" và key "access_token" như bên ProfileActivity
        SharedPreferences pref = getSharedPreferences("auth", MODE_PRIVATE);
        String token = pref.getString("access_token", "");

        if (token.isEmpty()) {
            android.util.Log.e("API_DEBUG", "Token rỗng, chuyển về Login");
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        ApiService api = RetrofitClient.getClient().create(ApiService.class);

        // 2. Gọi API với format "Bearer " + token
        api.getCurrentUser("Bearer " + token).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                android.util.Log.d("API_DEBUG", "Response Code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    // 3. Truy cập theo cấu trúc: body -> data -> user
                    UserResponse.Data data = response.body().getData();
                    if (data != null && data.getUser() != null) {
                        UserResponse.User admin = data.getUser();

                        // Hiển thị dữ liệu lên UI
                        tvAdminName.setText(admin.getFullName());
                        tvAdminEmail.setText(admin.getEmail());

                        android.util.Log.d("API_DEBUG", "Load thành công: " + admin.getFullName());
                    }
                } else {
                    // Nếu trả về 401, thông báo lỗi xác thực
                    android.util.Log.e("API_DEBUG", "Lỗi xác thực hoặc server: " + response.code());
                    Toast.makeText(AdminSettingActivity.this, "Phiên đăng nhập hết hạn", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                android.util.Log.e("API_DEBUG", "Lỗi kết nối: " + t.getMessage());
                Toast.makeText(AdminSettingActivity.this, "Lỗi kết nối server", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void setupEvents() {
        // Quản lý khách hàng
        if (itemManageUsers != null) {
            itemManageUsers.setOnClickListener(v -> startActivity(new Intent(this, AdminUserActivity.class)));
        }

        // Phê duyệt eKYC
        if (itemVerifyEkyc != null) {
            itemVerifyEkyc.setOnClickListener(v -> startActivity(new Intent(this, VerifyEkycActivity.class)));
        }

        // Điều chỉnh lãi suất
        if (itemAdjustRates != null) {
            itemAdjustRates.setOnClickListener(v -> startActivity(new Intent(this, AdminRatesActivity.class)));
        }

        // Đổi mật khẩu
        if (itemChangePassword != null) {
            itemChangePassword.setOnClickListener(v ->startActivity(new Intent(this, ChangePasswordActivity.class)));
        }

        // Đăng xuất
        if (cardLogout != null) {
            cardLogout.setOnClickListener(v -> {
                SharedPreferences pref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                pref.edit().clear().apply();

                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        }
    }
}