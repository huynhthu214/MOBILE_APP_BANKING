package com.example.zybanking;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.zybanking.ui.auth.LoginActivity;
import com.example.zybanking.ui.dashboard.AdminDashboardActivity;
import com.example.zybanking.ui.dashboard.HomeActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences pref = getSharedPreferences("auth", MODE_PRIVATE);
        String token = pref.getString("access_token", null);
        String role  = pref.getString("role", null);

        Intent intent;

        if (token == null || role == null) {
            // Chưa đăng nhập hoặc session lỗi
            intent = new Intent(this, LoginActivity.class);
        } else if ("admin".equalsIgnoreCase(role)) {
            // Admin
            intent = new Intent(this, AdminDashboardActivity.class);
        } else {
            // User thường
            intent = new Intent(this, HomeActivity.class);
        }

        startActivity(intent);
        finish(); // chặn quay lại MainActivity
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences pref = getSharedPreferences("auth", MODE_PRIVATE);
        pref.edit().clear().apply(); // xóa tất cả token/role
    }
}
