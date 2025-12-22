package com.example.zybanking.ui.transaction;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.zybanking.ui.dashboard.HomeActivity;
import com.example.zybanking.R;

public class TransferSuccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transaction_succes);

        double amount = getIntent().getDoubleExtra("AMOUNT", 0);
        TextView tvAmount = findViewById(R.id.tv_success_amount);
        Button btnHome = findViewById(R.id.btn_home_success);

        tvAmount.setText(java.text.NumberFormat.getInstance().format(amount) + " VND");

        btnHome.setOnClickListener(v -> {
            // Quay về màn hình chính và xóa các Activity trước đó trong stack
            Intent intent = new Intent(TransferSuccessActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}