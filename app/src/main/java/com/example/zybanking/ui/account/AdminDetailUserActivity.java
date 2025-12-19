package com.example.zybanking.ui.account;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.zybanking.HeaderAdmin;
import com.example.zybanking.R;

public class AdminDetailUserActivity extends HeaderAdmin {
    LinearLayout editInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_detail_users);
        initHeader();
        editInfo = findViewById(R.id.edit_info);
        editInfo.setOnClickListener(v -> startActivity(new Intent(this, AdminEditInforActivity.class)));
    }

}
