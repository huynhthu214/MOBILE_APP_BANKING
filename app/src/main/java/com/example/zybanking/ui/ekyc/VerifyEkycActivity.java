package com.example.zybanking.ui.ekyc;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide; // Cần thêm thư viện Glide trong build.gradle
import com.example.zybanking.HeaderAdmin;
import com.example.zybanking.R;
import com.example.zybanking.data.models.BasicResponse;
import com.example.zybanking.data.models.ekyc.EkycListResponse;
import com.example.zybanking.data.remote.ApiService;
import com.example.zybanking.data.remote.RetrofitClient;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyEkycActivity extends HeaderAdmin {
    private ImageView btnBack, imgFront, imgBack, imgSelfie;
    private TextView tvUserName, tvTime, tvKycInfo;
    private Button btnApprove, btnReject;

    private ApiService apiService;
    private String adminToken;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_ekyc);
        initHeader();

        initViews();
        setupData();
        loadPendingEkyc();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back_admin_ekyc);
        // Cập nhật ID chính xác theo layout admin_ekyc.xml của bạn
        btnApprove = findViewById(R.id.btn_approve); // Giả sử bạn đã đặt ID này trong XML
        btnReject = findViewById(R.id.btn_reject);

        // Gán sự kiện click
        if (btnApprove != null) btnApprove.setOnClickListener(v -> handleReview("approved"));
        if (btnReject != null) btnReject.setOnClickListener(v -> handleReview("rejected"));
        btnBack.setOnClickListener(v -> finish());
    }

    private void setupData() {
        apiService = RetrofitClient.getClient().create(ApiService.class);
        SharedPreferences sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        // Lấy token và thêm Bearer
        String token = sharedPref.getString("auth_token", "");
        adminToken = token.startsWith("Bearer ") ? token : "Bearer " + token;
    }

    private void loadPendingEkyc() {
        // Dòng 62: Đã truyền adminToken khớp với Header
        apiService.getPendingEkyc(adminToken).enqueue(new Callback<EkycListResponse>() {
            @Override
            public void onResponse(Call<EkycListResponse> call, Response<EkycListResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null && !response.body().getData().isEmpty()) {
                    displayData(response.body().getData().get(0));
                } else {
                    Toast.makeText(VerifyEkycActivity.this, "Không có hồ sơ chờ duyệt", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<EkycListResponse> call, Throwable t) {
                Toast.makeText(VerifyEkycActivity.this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayData(EkycListResponse.EkycItem item) {
        currentUserId = item.userId;
        // Gán dữ liệu vào UI (Cần đúng ID trong layout)
        // tvUserName.setText(item.fullName);
        // Glide.with(this).load(item.imgFront).into(imgFront);
    }

    private void handleReview(String status) {
        if (currentUserId == null) return;

        Map<String, Object> data = new HashMap<>();
        data.put("STATUS", status);
        data.put("REVIEWED_BY", "Admin_System");

        // Dòng 86: Truyền đủ 3 tham số: Token, UserId (Path), Data (Body)
        apiService.reviewEkyc(adminToken, currentUserId, data).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                if (response.isSuccessful()) {
                    String msg = status.equals("approved") ? "Đã duyệt hồ sơ!" : "Đã từ chối hồ sơ!";
                    Toast.makeText(VerifyEkycActivity.this, msg, Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                Toast.makeText(VerifyEkycActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }
}