package com.example.zybanking.ui.dashboard;

import android.os.Bundle;

import com.example.zybanking.NavbarActivity;
import com.example.zybanking.R;

public class HistoryActivity extends NavbarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.basic_history);

        initNavbar();
    }

}