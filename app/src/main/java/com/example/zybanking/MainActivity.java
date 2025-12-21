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
        pref.edit().clear().apply();

        // Sau khi xóa xong, các biến này sẽ luôn là null
        String token = pref.getString("access_token", null);
        String role  = pref.getString("role", null);

        // Logic bên dưới sẽ luôn chạy vào phần "token == null" -> Chuyển sang LoginActivity
        Intent intent;
        if (token == null || role == null) {
            intent = new Intent(this, LoginActivity.class);
        } else {
            String cleanRole = role.trim();
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