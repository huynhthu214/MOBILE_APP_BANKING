package com.example.zybanking;

import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.zybanking.ui.account.AdminSettingActivity;
import com.example.zybanking.ui.dashboard.AdminDashboardActivity;

public class HeaderAdmin extends AppCompatActivity {

    // Khai báo các biến khớp với XML
    protected ImageButton btnNotification;
    protected ImageButton btnAdmin;
    protected ImageView imgDashboard; // Đây sẽ đóng vai trò là nút Trái/Back luôn
    protected TextView tvTitle;       // Tiêu đề

    protected void initHeader() {
        // Ánh xạ ID từ XML
        btnNotification = findViewById(R.id.btnNotification);
        btnAdmin = findViewById(R.id.btn_admin);
        imgDashboard = findViewById(R.id.img_dashboard);
        tvTitle = findViewById(R.id.tvTitle);

        // 1. Chuyển sang setting
        if (btnAdmin != null) {
            btnAdmin.setOnClickListener(v -> startActivity(new Intent(this, AdminSettingActivity.class)));
        }

        // 2. Xử lý Thông báo
        if (btnNotification != null) {
            btnNotification.setOnClickListener(v -> {
                // Kiểm tra xem có đang ở màn hình Notification không để tránh mở chồng
                if (!(this instanceof NotificationAdminActivity)) {
                    Intent intent = new Intent(this, NotificationAdminActivity.class);
                    startActivity(intent);
                }
            });
        }

        // 3. Xử lý Click vào Dashboard (Mặc định về trang chủ)
        if (imgDashboard != null) {
            imgDashboard.setOnClickListener(v -> startActivity(new Intent(this, AdminDashboardActivity.class)));
        }
    }

    // === CÁC HÀM SETTER ĐỂ ACTIVITY CON GỌI ===

    public void setHeaderTitle(String title) {
        if (tvTitle != null) {
            tvTitle.setText(title);
        }
    }

    // Đổi icon nút bên trái (Ví dụ từ icon Dashboard thành icon Back)
    public void setLeftButtonImage(int resId) {
        if (imgDashboard != null) {
            imgDashboard.setImageResource(resId);
            imgDashboard.setVisibility(View.VISIBLE);
        }
    }

    // Đổi sự kiện click nút bên trái (Ví dụ từ về Home thành Finish/Back)
    public void setLeftButtonClickListener(View.OnClickListener listener) {
        if (imgDashboard != null) {
            imgDashboard.setOnClickListener(listener);
        }
    }
}