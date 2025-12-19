package com.example.zybanking.ui.account;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.HashMap;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.example.zybanking.data.remote.ApiService;
import com.example.zybanking.data.remote.RetrofitClient;
import com.example.zybanking.NavbarActivity;
import com.example.zybanking.R;
import com.example.zybanking.ui.auth.LoginActivity;
import com.google.android.material.button.MaterialButton;

public class ProfileActivity extends NavbarActivity {

    private TextView tvName, tvEmail, tvPhone;
    private MaterialButton btnLogout;
    private LinearLayout btnChangePassword, btnEkycStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        initViews();
        setupData();
        setupListeners();
        initNavbar();
    }

    private void initViews() {
        // Content
        tvName = findViewById(R.id.tv_profile_name);
        tvEmail = findViewById(R.id.tv_profile_email);
        tvPhone = findViewById(R.id.tv_profile_phone);

        btnLogout = findViewById(R.id.btn_logout);
        btnChangePassword = findViewById(R.id.btn_change_password);
        btnEkycStatus = findViewById(R.id.btn_ekyc_status);
    }

    private void setupData() {
        // Lấy dữ liệu từ SharedPreferences (hoặc dùng dữ liệu mẫu)
        SharedPreferences pref = getSharedPreferences("auth", MODE_PRIVATE);
        // String name = pref.getString("full_name", "Nguyen Van A");

        // Mock data
        tvName.setText("Nguyen Van A");
        tvEmail.setText("nguyenvana@gmail.com");
        tvPhone.setText("0987 654 321");
    }

    private void setupListeners() {
        // --- Xử lý các nút chức năng trong Profile ---
        btnLogout.setOnClickListener(v -> {

            SharedPreferences pref = getSharedPreferences("auth", MODE_PRIVATE);
            String accessToken = pref.getString("access_token", null);
            String refreshToken = pref.getString("refresh_token", null);

            if (refreshToken == null) {
                clearSessionAndGoLogin();
                return;
            }

            ApiService apiService = RetrofitClient
                    .getClient()
                    .create(ApiService.class);

            Map<String, String> body = new HashMap<>();
            body.put("refresh_token", refreshToken);

            apiService.logout("Bearer " + accessToken, body)
                    .enqueue(new Callback<Map<String, Object>>() {
                        @Override
                        public void onResponse(Call<Map<String, Object>> call,
                                               Response<Map<String, Object>> response) {
                            clearSessionAndGoLogin();
                        }

                        @Override
                        public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                            clearSessionAndGoLogin();
                        }
                    });
        });

        btnChangePassword.setOnClickListener(v ->
                Toast.makeText(this, "Chức năng Đổi mật khẩu", Toast.LENGTH_SHORT).show()
        );

        btnEkycStatus.setOnClickListener(v ->
                Toast.makeText(this, "Thông tin eKYC", Toast.LENGTH_SHORT).show()
        );

    }
    private void clearSessionAndGoLogin() {
        SharedPreferences pref = getSharedPreferences("auth", MODE_PRIVATE);
        pref.edit().clear().apply();

        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

}