package com.example.zybanking.ui.transaction;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.zybanking.R;

public class DepositActivity extends AppCompatActivity {
    ImageView btnBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deposit);

        btnBack = findViewById(R.id.btn_back_deposit);
        btnBack.setOnClickListener(v -> finish());
    }
}
