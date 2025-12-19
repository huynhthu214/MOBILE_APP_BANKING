package com.example.zybanking.ui.utilities;

import android.os.Bundle;
import android.widget.LinearLayout;

import com.example.zybanking.NavbarActivity;
import com.example.zybanking.R;

public class UtilitiesActivity extends NavbarActivity {
    LinearLayout btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.basic_uti);

        btnBack = findViewById(R.id.btn_back_uti);
        btnBack.setOnClickListener(v -> finish());
        initNavbar();
    }
}
