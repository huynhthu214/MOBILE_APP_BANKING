package com.example.zybanking;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.example.zybanking.ui.account.ProfileActivity;
import com.example.zybanking.ui.dashboard.HistoryActivity;
import com.example.zybanking.ui.dashboard.HomeActivity;
import com.example.zybanking.ui.transaction.TransactionActivity;

public class NavbarActivity extends AppCompatActivity {

    protected LinearLayout navHome, navHistory, navTransaction, navProfile, navNoti;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void initNavbar() {
        navHome = findViewById(R.id.nav_home);
        navHistory = findViewById(R.id.nav_history);
        navTransaction = findViewById(R.id.nav_transaction);
        navProfile = findViewById(R.id.nav_profile);
        navNoti = findViewById(R.id.nav_noti);

        if (navHome != null) navHome.setOnClickListener(v -> startActivity(new Intent(this, HomeActivity.class)));
        if (navHistory != null) navHistory.setOnClickListener(v -> startActivity(new Intent(this, HistoryActivity.class)));
        if (navTransaction != null) navTransaction.setOnClickListener(v -> startActivity(new Intent(this, TransactionActivity.class)));
        if (navProfile != null) navProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        if (navNoti != null) navNoti.setOnClickListener(v -> startActivity(new Intent(this, NotificationUserActivity.class)));
    }

}
