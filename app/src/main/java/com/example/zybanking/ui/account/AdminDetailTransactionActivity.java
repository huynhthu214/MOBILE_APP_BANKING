package com.example.zybanking.ui.account;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.zybanking.R;

public class AdminDetailTransactionActivity extends AppCompatActivity {
    ImageView btnBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_detail_transaction);
        btnBack = findViewById(R.id.btn_back_admin_detail);
        btnBack.setOnClickListener(v -> finish());
    }
}
