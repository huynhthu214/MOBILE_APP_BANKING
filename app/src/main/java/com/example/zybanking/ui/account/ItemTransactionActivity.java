package com.example.zybanking.ui.account;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.zybanking.R;

public class ItemTransactionActivity extends AppCompatActivity {
    ConstraintLayout detailTransac;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_item_transactions);

        detailTransac = findViewById(R.id.detail_transac);
        detailTransac.setOnClickListener(v -> startActivity(new Intent(this, AdminDetailTransactionActivity.class)));
    }
}
