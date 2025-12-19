package com.example.zybanking.ui.account;

import android.content.Intent;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import com.example.zybanking.HeaderAdmin;
import com.example.zybanking.R;
import com.example.zybanking.ui.auth.LoginActivity;

public class AdminSettingActivity extends HeaderAdmin {
    private CardView btnLogout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_setting);
        initHeader();

        btnLogout = findViewById(R.id.card_logout);
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                logout();
            });
        }
    }
    private void logout() {
        // A. Xóa thông tin đăng nhập (nếu bạn có lưu trong SharedPreferences)
        // Ví dụ:
        // SharedPreferences preferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        // SharedPreferences.Editor editor = preferences.edit();
        // editor.clear();
        // editor.apply();

        // B. Chuyển về màn hình Login
        Intent intent = new Intent(AdminSettingActivity.this, LoginActivity.class);

        // C. Xóa hết các Activity cũ trong Stack để người dùng không bấm Back quay lại được
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
        finish();
    }
}
