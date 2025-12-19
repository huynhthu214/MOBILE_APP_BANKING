package com.example.zybanking.ui.account;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.zybanking.R;

public class ItemUserActivity extends AppCompatActivity {
    RelativeLayout detailUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_item_users);

        detailUser = findViewById(R.id.detail_users);
        detailUser.setOnClickListener(v -> startActivity(new Intent(this, AdminDetailUserActivity.class)));
    }
}
