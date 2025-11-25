package com.example.zybanking;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class DashboardActivity extends AppCompatActivity {

    private TextView tvUserName, tvCurrentBalance;
    private FloatingActionButton fabTransfer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

        // Ánh xạ view
        tvUserName = findViewById(R.id.tv_user_name);
        tvCurrentBalance = findViewById(R.id.tv_current_balance);
        fabTransfer = findViewById(R.id.fab_transfer);

        // Nhận dữ liệu fake từ LoginActivity
        String userName = getIntent().getStringExtra("user_name");
        if (userName != null) {
            tvUserName.setText(userName + "!");
        }

        // Dữ liệu fake: balance
        tvCurrentBalance.setText("$4,570.80");

        // Click FAB để thông báo
        fabTransfer.setOnClickListener(v ->
                Toast.makeText(DashboardActivity.this, "Chức năng chuyển tiền chưa triển khai", Toast.LENGTH_SHORT).show()
        );

        // Bạn có thể mở rộng: thêm sự kiện cho các nút, CardView, thanh tìm kiếm, thông báo...
    }
}
