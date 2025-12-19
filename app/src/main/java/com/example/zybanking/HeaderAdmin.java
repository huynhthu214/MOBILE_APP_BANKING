package com.example.zybanking;

import android.content.Intent;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.zybanking.ui.account.AdminSettingActivity;
import com.example.zybanking.ui.dashboard.AdminDashboardActivity;

public class HeaderAdmin extends AppCompatActivity {

    protected ImageButton btnNotification;
    protected ImageButton btnAdmin;
    protected ImageView imgDashboard;

    protected void initHeader() {
        btnNotification = findViewById(R.id.btnNotification);
        btnAdmin = findViewById(R.id.btn_admin);
        imgDashboard = findViewById(R.id.img_dashboard);
        // 1. Chuyển sang setting
        if (btnAdmin != null) {
            btnAdmin.setOnClickListener(v -> startActivity(new Intent(this, AdminSettingActivity.class)));
        }

        // 2. Xử lý Thông báo
        if (btnNotification != null) {
            btnNotification.setOnClickListener(v -> {
                Intent intent = new Intent(this, NotificationActivity.class);
                startActivity(intent);
            });
        }

        // 3. Xử lý Click vào (Về trang chủ Dashboard)
        if (imgDashboard != null) {
            imgDashboard.setOnClickListener(v -> startActivity(new Intent(this, AdminDashboardActivity.class)));
        }
    }
}