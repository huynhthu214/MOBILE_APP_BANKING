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

        // 1. Lấy SharedPreferences (KHÔNG ĐƯỢC GỌI clear() Ở ĐÂY)
        SharedPreferences pref = getSharedPreferences("auth", MODE_PRIVATE);

        // 2. Lấy token và role đã lưu từ lúc Login
        String token = pref.getString("access_token", null);
        String role  = pref.getString("role", null);

        Intent intent;

        // 3. Logic điều hướng
        if (token == null || role == null) {
            // Chưa đăng nhập (hoặc mất token) -> Về Login
            intent = new Intent(this, LoginActivity.class);
        } else {
            // Đã đăng nhập -> Kiểm tra quyền để về trang Dashboard tương ứng
            String cleanRole = role.trim();
            if ("admin".equalsIgnoreCase(cleanRole) || "administrator".equalsIgnoreCase(cleanRole)) {
                intent = new Intent(this, AdminDashboardActivity.class);
            } else {
                // Đây là trang Home của khách hàng (User)
                intent = new Intent(this, HomeActivity.class);
            }
        }

        // 4. Chuyển màn hình và đóng MainActivity lại
        startActivity(intent);
        finish();
    }
}