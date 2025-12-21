package com.example.zybanking.ui.ekyc;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zybanking.HeaderAdmin;
import com.example.zybanking.R;
import com.example.zybanking.data.adapter.AdminEkycAdapter;
import com.example.zybanking.data.models.BasicResponse;
import com.example.zybanking.data.models.ekyc.EkycListResponse;
import com.example.zybanking.data.remote.ApiService;
import com.example.zybanking.data.remote.RetrofitClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyEkycActivity extends HeaderAdmin {

    private RecyclerView rvPending;
    private AdminEkycAdapter adapter;
    private List<EkycListResponse.EkycItem> pendingList = new ArrayList<>();

    private ApiService apiService;
    private String adminToken;

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
        ImageView btnBack = findViewById(R.id.btn_back_admin_ekyc);
        btnBack.setOnClickListener(v -> finish());

        rvPending = findViewById(R.id.rv_pending_ekyc);
        rvPending.setLayoutManager(new LinearLayoutManager(this));

        // Khởi tạo Adapter với Callback xử lý sự kiện
        adapter = new AdminEkycAdapter(pendingList, new AdminEkycAdapter.OnItemClickListener() {
            @Override
            public void onClickDetail(EkycListResponse.EkycItem item) {
                // Truyền dữ liệu text sang màn hình chi tiết
                AdminEkycDetailActivity.selectedItem = item;

                // Chuyển màn hình
                Intent intent = new Intent(VerifyEkycActivity.this, AdminEkycDetailActivity.class);
                detailLauncher.launch(intent);
            }
        });

        rvPending.setAdapter(adapter);
    }
    // Khai báo Launcher để nhận kết quả khi duyệt xong
    private final androidx.activity.result.ActivityResultLauncher<Intent> detailLauncher = registerForActivityResult(
            new androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    // Nếu bên chi tiết đã duyệt/từ chối -> Reload lại danh sách
                    loadPendingEkyc();
                }
            }
    );

    private void setupData() {
        apiService = RetrofitClient.getClient().create(ApiService.class);
        SharedPreferences sharedPref = getSharedPreferences("auth", Context.MODE_PRIVATE);
        String token = sharedPref.getString("access_token", "");
        adminToken = token.startsWith("Bearer ") ? token : "Bearer " + token;
    }

    private void loadPendingEkyc() {
        apiService.getPendingEkyc(adminToken).enqueue(new Callback<EkycListResponse>() {
            @Override
            public void onResponse(Call<EkycListResponse> call, Response<EkycListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<EkycListResponse.EkycItem> data = response.body().getData();
                    if (data != null && !data.isEmpty()) {
                        pendingList.clear();
                        pendingList.addAll(data);
                        adapter.updateList(pendingList);
                    } else {
                        Toast.makeText(VerifyEkycActivity.this, "Không có hồ sơ chờ duyệt", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(VerifyEkycActivity.this, "Lỗi tải dữ liệu: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<EkycListResponse> call, Throwable t) {
                Toast.makeText(VerifyEkycActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleReview(String userId, String status) {
        Map<String, Object> data = new HashMap<>();
        data.put("STATUS", status);
        data.put("REVIEWED_BY", "Admin"); // Hoặc lấy tên Admin từ SharedPrefs

        apiService.reviewEkyc(adminToken, userId, data).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                if (response.isSuccessful()) {
                    String msg = status.equals("approved") ? "Đã duyệt!" : "Đã từ chối!";
                    Toast.makeText(VerifyEkycActivity.this, msg, Toast.LENGTH_SHORT).show();

                    // Load lại danh sách sau khi duyệt xong
                    loadPendingEkyc();
                } else {
                    Toast.makeText(VerifyEkycActivity.this, "Lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                Toast.makeText(VerifyEkycActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }
}