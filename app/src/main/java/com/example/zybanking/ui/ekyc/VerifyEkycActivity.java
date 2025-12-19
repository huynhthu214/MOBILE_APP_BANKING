package com.example.zybanking.ui.ekyc;

import android.os.Bundle;
import android.widget.ImageView;


import com.example.zybanking.HeaderAdmin;
import com.example.zybanking.R;

public class VerifyEkycActivity extends HeaderAdmin {
    ImageView btnBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_ekyc);
        initHeader();
        btnBack = findViewById(R.id.btn_back_admin_ekyc);
        btnBack.setOnClickListener(v -> finish());
    }
}
