package com.example.zybanking.ui.map;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.zybanking.R;
import com.google.android.material.appbar.MaterialToolbar;

public class MapActivity extends AppCompatActivity {
    MaterialToolbar btnBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.map);

        btnBack = findViewById(R.id.toolbar_map);
        btnBack.setOnClickListener(v -> finish());
    }
}