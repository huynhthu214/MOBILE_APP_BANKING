package com.example.zybanking;

import android.content.Intent;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.zybanking.ui.auth.LoginActivity;
import com.example.zybanking.ui.dashboard.AdminDashboardActivity;
import com.example.zybanking.NotificationActivity;

public class HeaderAdmin extends AppCompatActivity {

    protected ImageButton btnNotification;
    protected ImageButton btnLogout;
    protected TextView tvTitle;
    protected TextView tvSubtitle;

    protected void initHeader() {
        btnNotification = findViewById(R.id.btnNotification);
        btnLogout = findViewById(R.id.btnLogout);
        tvTitle = findViewById(R.id.tvTitle);
        tvSubtitle = findViewById(R.id.tvSubtitle);

        // 1. Xử lý Đăng xuất
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                Intent intent = new Intent(this, LoginActivity.class);
                // Xóa lịch sử activity để không thể back lại
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        }

        // 2. Xử lý Thông báo
        if (btnNotification != null) {
            btnNotification.setOnClickListener(v -> {
                Intent intent = new Intent(this, NotificationActivity.class);
                startActivity(intent);
            });
        }

        // 3. Xử lý Click vào Tiêu đề (Về trang chủ Dashboard)
        if (tvTitle != null) {
            tvTitle.setOnClickListener(v -> {
                // Kiểm tra: Nếu KHÔNG PHẢI là AdminDashboardActivity thì mới chuyển trang
                // Để tránh việc đang ở trang chủ mà bấm vào nó lại load lại trang chủ
                if (!(this instanceof AdminDashboardActivity)) {
                    Intent intent = new Intent(this, AdminDashboardActivity.class);
                    // Clear top để xóa các activity nằm trên dashboard (nếu có)
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                }
            });
        }
    }
}