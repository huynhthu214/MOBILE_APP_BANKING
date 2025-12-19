package com.example.zybanking.ui.transaction;

import android.os.Bundle;
import android.widget.ImageView;

import com.example.zybanking.HeaderAdmin;
import com.example.zybanking.R;

public class AdminTransaction extends HeaderAdmin {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_transactions);

        initHeader();
    }
}
