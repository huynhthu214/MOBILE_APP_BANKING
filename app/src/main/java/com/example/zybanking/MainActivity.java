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

        // --- THÊM LOG ĐỂ KIỂM TRA ---
        if (role != null) {
            // Xem Logcat với từ khóa "DEBUG_ROLE" để biết chính xác role là gì
            android.util.Log.e("DEBUG_ROLE", "Role hiện tại là: '" + role + "'");
            android.util.Log.e("DEBUG_ROLE", "So sánh với admin: " + "admin".equalsIgnoreCase(role));
        } else {
            android.util.Log.e("DEBUG_ROLE", "Role bị NULL");
        }
        // -----------------------------

        Intent intent;

        if (token == null || role == null) {
            intent = new Intent(this, LoginActivity.class);
        } else {
            // Dùng trim() để cắt bỏ khoảng trắng thừa nếu có
            String cleanRole = role.trim();

            // Kiểm tra các trường hợp có thể xảy ra của admin
            if ("admin".equalsIgnoreCase(cleanRole) || "administrator".equalsIgnoreCase(cleanRole)) {
                intent = new Intent(this, AdminDashboardActivity.class);
            } else {
                intent = new Intent(this, HomeActivity.class);
            }
        }

        startActivity(intent);
        finish();
    }
}
