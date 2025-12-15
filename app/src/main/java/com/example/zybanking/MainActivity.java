package com.example.zybanking;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences pref = getSharedPreferences("auth", MODE_PRIVATE);
        String token = pref.getString("access_token", null);

        if (token == null) {
            // chưa login
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            // đã login
            startActivity(new Intent(this, DashboardActivity.class));
        }

        finish();
    }
}
