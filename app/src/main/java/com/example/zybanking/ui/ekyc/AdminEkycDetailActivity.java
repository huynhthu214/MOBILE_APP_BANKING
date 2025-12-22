package com.example.zybanking.ui.ekyc;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zybanking.HeaderAdmin;
import com.example.zybanking.R;
import com.example.zybanking.data.models.BasicResponse;
import com.example.zybanking.data.models.ekyc.EkycListResponse;
import com.example.zybanking.data.models.ekyc.EkycResponse;
import com.example.zybanking.data.remote.ApiService;
import com.example.zybanking.data.remote.RetrofitClient;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminEkycDetailActivity extends HeaderAdmin {
    public static EkycListResponse.EkycItem selectedItem;

    private ImageView imgFront, imgBack, imgSelfie, btnBack;
    private TextView tvName, tvInfo;
    private Button btnApprove, btnReject;
    private ApiService apiService;
    private String adminToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_ekyc_detail);

        if (selectedItem == null) {
            finish();
            return;
        }

        initViews();

        // 1. Hiển thị thông tin Text trước (có sẵn từ list)
        tvName.setText(selectedItem.fullName);
        tvInfo.setText("User ID: " + selectedItem.userId + "\nEmail: " + selectedItem.email);

        setupApi();
        // 2. GỌI API ĐỂ LẤY ẢNH (Thay vì lấy từ selectedItem)
        loadEkycImages(selectedItem.userId);

        btnBack = findViewById(R.id.btn_back_detail);
        btnBack.setOnClickListener(v -> finish());
    }
    private void loadEkycImages(String userId) {
        // Gọi API lấy chi tiết EKYC của user
        apiService.getMyEkyc(userId).enqueue(new Callback<EkycResponse>() { // Dùng class EkycResponse chuẩn
            @Override
            public void onResponse(Call<EkycResponse> call, Response<EkycResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                    EkycResponse.EkycData data = response.body().data;

                    // Hiển thị ảnh (Sử dụng hàm displayBase64 đã được tối ưu)
                    displayBase64(data.frontUrl, imgFront);
                    displayBase64(data.backUrl, imgBack);
                    displayBase64(data.selfieUrl, imgSelfie);
                } else {
                    Toast.makeText(AdminEkycDetailActivity.this, "Không có dữ liệu ảnh", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<EkycResponse> call, Throwable t) {
                Toast.makeText(AdminEkycDetailActivity.this, "Lỗi tải ảnh: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initViews() {
        findViewById(R.id.btn_back_detail).setOnClickListener(v -> finish());

        tvName = findViewById(R.id.detail_tv_name);
        tvInfo = findViewById(R.id.detail_tv_info);
        imgFront = findViewById(R.id.detail_img_front);
        imgBack = findViewById(R.id.detail_img_back);
        imgSelfie = findViewById(R.id.detail_img_selfie);

        btnApprove = findViewById(R.id.detail_btn_approve);
        btnReject = findViewById(R.id.detail_btn_reject);

        btnApprove.setOnClickListener(v -> handleReview("approved"));
        btnReject.setOnClickListener(v -> handleReview("rejected"));
    }

    private void displayData() {
        tvName.setText(selectedItem.fullName);
        tvInfo.setText("User ID: " + selectedItem.userId + "\nEmail: " + selectedItem.email);

        displayBase64(selectedItem.imgFront, imgFront);
        displayBase64(selectedItem.imgBack, imgBack);
        displayBase64(selectedItem.selfie, imgSelfie);
    }

    private void displayBase64(String base64Str, ImageView imgView) {
        if (base64Str == null || base64Str.isEmpty()) {
            // Nếu không có ảnh, set ảnh mặc định hoặc ẩn đi
            imgView.setImageResource(R.drawable.ic_image_placeholder); // Tạo ảnh placeholder nếu cần
            return;
        }
        try {
            String cleanBase64 = base64Str;
            if (base64Str.contains(",")) cleanBase64 = base64Str.split(",")[1];
            cleanBase64 = cleanBase64.replaceAll("\\s+", "");

            byte[] decodedString = Base64.decode(cleanBase64, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

            if (decodedByte != null) {
                imgView.setImageBitmap(decodedByte);
                imgView.setScaleType(ImageView.ScaleType.FIT_CENTER); // Dùng FIT_CENTER để thấy toàn bộ ảnh CMND
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupApi() {
        apiService = RetrofitClient.getClient().create(ApiService.class);
        SharedPreferences sharedPref = getSharedPreferences("auth", MODE_PRIVATE);
        String token = sharedPref.getString("access_token", "");
        adminToken = token.startsWith("Bearer ") ? token : "Bearer " + token;
    }

    private void handleReview(String status) {
        Map<String, Object> data = new HashMap<>();
        data.put("STATUS", status);
        data.put("REVIEWED_BY", "Admin");

        apiService.reviewEkyc(adminToken, selectedItem.userId, data).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                if (response.isSuccessful()) {
                    String msg = status.equals("approved") ? "Đã duyệt thành công!" : "Đã từ chối hồ sơ!";
                    Toast.makeText(AdminEkycDetailActivity.this, msg, Toast.LENGTH_SHORT).show();

                    // Đóng màn hình này, trả về kết quả OK để màn hình trước reload lại list
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(AdminEkycDetailActivity.this, "Lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                Toast.makeText(AdminEkycDetailActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }
}