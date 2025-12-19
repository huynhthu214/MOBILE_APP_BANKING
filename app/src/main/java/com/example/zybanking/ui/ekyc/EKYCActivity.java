package com.example.zybanking.ui.ekyc;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import com.example.zybanking.R;

public class EKYCActivity extends AppCompatActivity {
    ImageView btnBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.basic_ekyc);

        btnBack = findViewById(R.id.btn_back_ekyc);
        btnBack.setOnClickListener(v -> finish());
    }
}
