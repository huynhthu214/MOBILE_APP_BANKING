package com.example.zybanking.ui.account;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.zybanking.R;
import com.example.zybanking.data.models.BasicResponse;
import com.example.zybanking.data.remote.ApiService;
import com.example.zybanking.data.remote.RetrofitClient;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminRatesActivity extends AppCompatActivity {
    private EditText et1m, et6m, et12m, etMortgage;
    private Button btnUpdate;
    private ImageView btnBack;
    private ApiService apiService;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_rates);

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
        etMortgage = findViewById(R.id.et_rate_mortgage); // Đảm bảo ID này có trong XML
        btnUpdate = findViewById(R.id.btn_update_rates);
        btnBack = findViewById(R.id.btn_back_rates);
    }

    private void setupData() {
        apiService = RetrofitClient.getClient().create(ApiService.class);
        SharedPreferences pref = getSharedPreferences("auth", Context.MODE_PRIVATE);
        String rawToken = pref.getString("access_token", "");
        token = rawToken.startsWith("Bearer ") ? rawToken : "Bearer " + rawToken;
    }

    private void loadCurrentRates() {
        apiService.getInterestRates(token).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> body = response.body();

                    if (body.get("data") != null) {
                        // Gson thường chuyển JSON object con thành LinkedTreeMap
                        Object dataObj = body.get("data");
                        Map<String, Object> rates = (Map<String, Object>) dataObj;

                        et1m.setText(String.valueOf(rates.get("rate_1m")));
                        et6m.setText(String.valueOf(rates.get("rate_6m")));
                        et12m.setText(String.valueOf(rates.get("rate_12m")));

                        if (rates.get("rate_mortgage") != null) {
                            etMortgage.setText(String.valueOf(rates.get("rate_mortgage")));
                        }
                    }
                } else {
                    Toast.makeText(AdminRatesActivity.this, "Không tải được dữ liệu", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Log.e("AdminRates", "Error: " + t.getMessage());
                Toast.makeText(AdminRatesActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleUpdateRates() {
        Map<String, Double> newRates = new HashMap<>();
        try {
            // Lấy các lãi suất tiết kiệm
            newRates.put("rate_1m", Double.parseDouble(et1m.getText().toString()));
            newRates.put("rate_6m", Double.parseDouble(et6m.getText().toString()));
            newRates.put("rate_12m", Double.parseDouble(et12m.getText().toString()));

            // Lấy lãi suất vay thế chấp từ ô nhập liệu
            // Khi gửi cái này lên, Server sẽ update cột INTEREST_RATE trong bảng ACCOUNT
            newRates.put("rate_mortgage", Double.parseDouble(etMortgage.getText().toString()));

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Vui lòng nhập số hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        // Gọi API PUT
        apiService.updateInterestRates(token, newRates).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminRatesActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AdminRatesActivity.this, "Lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                Toast.makeText(AdminRatesActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }
}