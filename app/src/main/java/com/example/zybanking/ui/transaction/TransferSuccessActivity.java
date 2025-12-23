package com.example.zybanking.ui.transaction;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.zybanking.ui.dashboard.HomeActivity;
import com.example.zybanking.R;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TransferSuccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transaction_succes); // Đảm bảo đúng tên file XML

        // Ánh xạ View
        TextView tvAmount = findViewById(R.id.tv_success_amount);
        TextView tvRecipient = findViewById(R.id.tv_success_recipient);
        TextView tvTxCode = findViewById(R.id.tv_success_tx_code);
        TextView tvTime = findViewById(R.id.tv_success_time);
        Button btnHome = findViewById(R.id.btn_home_success);

        // Lấy dữ liệu từ Intent (được truyền từ ConfirmTransactionActivity)
        double amount = getIntent().getDoubleExtra("AMOUNT", 0);
        String recipient = getIntent().getStringExtra("RECIPIENT_ID");
        // String txId = getIntent().getStringExtra("TX_ID"); // Nếu bạn có truyền

        // Format tiền tệ
        tvAmount.setText(NumberFormat.getInstance().format(amount) + " VND");

        // Hiển thị thông tin khác
        if(recipient != null) tvRecipient.setText(recipient);

        // Tự động lấy giờ hiện tại
        String currentTime = new SimpleDateFormat("dd/MM/yyyy, HH:mm", Locale.getDefault()).format(new Date());
        tvTime.setText(currentTime);

        // Nút về trang chủ
        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(TransferSuccessActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}