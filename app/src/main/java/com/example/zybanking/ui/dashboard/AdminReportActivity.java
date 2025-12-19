package com.example.zybanking.ui.dashboard;

import android.os.Bundle;

import com.example.zybanking.HeaderAdmin;
import com.example.zybanking.R;

public class AdminReportActivity extends HeaderAdmin {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_report);

        initHeader();
    }
}
