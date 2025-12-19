package com.example.zybanking.ui.account;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.zybanking.HeaderAdmin;
import com.example.zybanking.R;

public class AdminUserActivity extends HeaderAdmin {
    ImageView addUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_user);
        initHeader();
        addUser = findViewById(R.id.add_user);
        addUser.setOnClickListener(v -> startActivity(new Intent(this, CreateAccountActivity.class)));
    }
}
