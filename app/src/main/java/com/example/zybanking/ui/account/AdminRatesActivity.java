package com.example.zybanking.ui.account;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.zybanking.R;

public class AdminRatesActivity extends AppCompatActivity {
    ImageView btnBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_rates);
        btnBack = findViewById(R.id.btn_back_rates);
        btnBack.setOnClickListener(v -> finish());
    }
}
