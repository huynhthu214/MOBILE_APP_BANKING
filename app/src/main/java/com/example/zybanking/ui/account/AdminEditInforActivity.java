package com.example.zybanking.ui.account;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.zybanking.HeaderAdmin;
import com.example.zybanking.R;

public class AdminEditInforActivity extends HeaderAdmin {
    ImageView btnBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_edit_infor_user);
        btnBack = findViewById(R.id.btn_back_edit);
        btnBack.setOnClickListener(v -> finish());
    }
}
