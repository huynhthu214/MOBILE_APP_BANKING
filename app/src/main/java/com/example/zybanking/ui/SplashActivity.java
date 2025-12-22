package com.example.zybanking.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import com.example.zybanking.R;
import com.example.zybanking.ui.auth.LoginActivity;
import com.example.zybanking.ui.dashboard.HomeActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Đợi 2 giây sau đó kiểm tra logic chuyển màn hình
        new Handler().postDelayed(() -> {
            checkLoginStatus();
        }, 2000);
    }

    private void checkLoginStatus() {
        SharedPreferences pref = getSharedPreferences("auth", MODE_PRIVATE);
        String token = pref.getString("access_token", "");

        if (token != null && !token.isEmpty()) {
            // Đã có token -> Vào thẳng trang chủ
            startActivity(new Intent(SplashActivity.this, HomeActivity.class));
        } else {
            // Chưa đăng nhập -> Vào trang Login
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
        }
        finish(); // Đóng màn hình Splash để không quay lại được bằng nút Back
    }
}