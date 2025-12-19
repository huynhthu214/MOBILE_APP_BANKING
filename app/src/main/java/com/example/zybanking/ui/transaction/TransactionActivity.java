package com.example.zybanking.ui.transaction;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.zybanking.NavbarActivity;
import com.example.zybanking.R;

public class TransactionActivity extends NavbarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.basic_transfer);

        initNavbar();
    }
}