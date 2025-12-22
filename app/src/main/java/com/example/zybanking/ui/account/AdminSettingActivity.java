package com.example.zybanking.ui.account;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.cardview.widget.CardView;

import com.example.zybanking.HeaderAdmin;
import com.example.zybanking.R;
import com.example.zybanking.data.models.auth.User;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_setting);

        initHeader();
        initViews();
        setupEvents();

        // Load dữ liệu ngay khi mở màn hình
        loadAdminProfile();
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

    private void loadAdminProfile() {
        // 1. Lấy token từ file "auth" và key "access_token" (Đồng bộ với Login)
        SharedPreferences pref = getSharedPreferences("auth", MODE_PRIVATE);
        String token = pref.getString("access_token", "");

        if (token.isEmpty()) {
            Log.e("AdminSetting", "Token rỗng! Chuyển về login.");
            redirectToLogin();
            return;
        }

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

        // 2. Gọi API lấy thông tin
        apiService.getCurrentUser("Bearer " + token).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserResponse.Data data = response.body().getData();

                    if (data != null && data.getUser() != null) {
                        User admin = data.getUser();

                        // 3. Hiển thị thông tin
                        tvAdminName.setText(admin.getFullName());
                        tvAdminEmail.setText(admin.getEmail());
                        Log.d("AdminSetting", "Load thành công: " + admin.getFullName());
                    }
                } else {
                    // 4. Xử lý lỗi 401 (Token hết hạn/Không hợp lệ)
                    if (response.code() == 401) {
                        Toast.makeText(AdminSettingActivity.this, "Phiên đăng nhập hết hạn", Toast.LENGTH_SHORT).show();
                        redirectToLogin();
                    } else {
                        Log.e("AdminSetting", "Lỗi server: " + response.code());
                    }
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(AdminSettingActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                Log.e("AdminSetting", "Failure: " + t.getMessage());
            }
        });
    }

    private void setupEvents() {
        if (itemManageUsers != null) itemManageUsers.setOnClickListener(v -> startActivity(new Intent(this, AdminUserActivity.class)));
        if (itemVerifyEkyc != null) itemVerifyEkyc.setOnClickListener(v -> startActivity(new Intent(this, VerifyEkycActivity.class)));
        if (itemAdjustRates != null) itemAdjustRates.setOnClickListener(v -> startActivity(new Intent(this, AdminRatesActivity.class)));
        if (itemChangePassword != null) itemChangePassword.setOnClickListener(v -> startActivity(new Intent(this, ChangePasswordActivity.class)));

        if (cardLogout != null) {
            cardLogout.setOnClickListener(v -> {
                // 5. Xóa sạch token ở CẢ HAI file để đảm bảo không bị conflict sau này
                getSharedPreferences("auth", Context.MODE_PRIVATE).edit().clear().apply();
                getSharedPreferences("UserPrefs", Context.MODE_PRIVATE).edit().clear().apply();

                redirectToLogin();
            });
        }
    }

    private void redirectToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}