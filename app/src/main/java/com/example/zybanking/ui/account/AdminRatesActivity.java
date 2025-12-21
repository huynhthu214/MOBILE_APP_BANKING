package com.example.zybanking.ui.account;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.zybanking.R;
import com.example.zybanking.data.models.BasicResponse;
import com.example.zybanking.data.remote.ApiService;
import com.example.zybanking.data.remote.RetrofitClient;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminRatesActivity extends AppCompatActivity {
    private EditText et1m, et6m, et12m;
    private Button btnUpdate;
    private ImageView btnBack;
    private ApiService apiService;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_rates); // Tên file XML bạn cung cấp

        initViews();
        setupData();
        loadCurrentRates();

        btnBack.setOnClickListener(v -> finish());
        btnUpdate.setOnClickListener(v -> handleUpdateRates());
    }

    private void initViews() {
        et1m = findViewById(R.id.et_rate_1m);
        et6m = findViewById(R.id.et_rate_6m);
        et12m = findViewById(R.id.et_rate_12m);
        btnUpdate = findViewById(R.id.btn_update_rates);
        btnBack = findViewById(R.id.btn_back_rates);
    }

    private void setupData() {
        apiService = RetrofitClient.getClient().create(ApiService.class);
        SharedPreferences pref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String rawToken = pref.getString("auth_token", "");
        token = rawToken.startsWith("Bearer ") ? rawToken : "Bearer " + rawToken;
    }

    private void loadCurrentRates() {
        apiService.getInterestRates(token).enqueue(new Callback<Map<String, Double>>() {
            @Override
            public void onResponse(Call<Map<String, Double>> call, Response<Map<String, Double>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    et1m.setText(String.valueOf(response.body().get("rate_1m")));
                    et6m.setText(String.valueOf(response.body().get("rate_6m")));
                    et12m.setText(String.valueOf(response.body().get("rate_12m")));
                }
            }
            @Override
            public void onFailure(Call<Map<String, Double>> call, Throwable t) {
                Toast.makeText(AdminRatesActivity.this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleUpdateRates() {
        Map<String, Double> newRates = new HashMap<>();
        try {
            newRates.put("rate_1m", Double.parseDouble(et1m.getText().toString()));
            newRates.put("rate_6m", Double.parseDouble(et6m.getText().toString()));
            newRates.put("rate_12m", Double.parseDouble(et12m.getText().toString()));
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Vui lòng nhập số hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        apiService.updateInterestRates(token, newRates).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminRatesActivity.this, "Cập nhật lãi suất thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AdminRatesActivity.this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                Toast.makeText(AdminRatesActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }
}