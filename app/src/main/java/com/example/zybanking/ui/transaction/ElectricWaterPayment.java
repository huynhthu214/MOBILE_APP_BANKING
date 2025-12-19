package com.example.zybanking.ui.transaction;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.zybanking.R;

public class ElectricWaterPayment extends AppCompatActivity {
    ImageView btnBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.electric_water_payment);

        btnBack = findViewById(R.id.btn_back_bill);
        btnBack.setOnClickListener(v -> finish());
    }
}
