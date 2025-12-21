package com.example.zybanking;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zybanking.data.models.BasicResponse;
import com.example.zybanking.data.models.Notification;
import com.example.zybanking.data.remote.ApiService;
import com.example.zybanking.data.remote.RetrofitClient;
import com.example.zybanking.data.adapter.NotificationAdapter; // Import Adapter vừa tạo
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationUserActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private RecyclerView rvNotifications;
    private LinearLayout layoutEmptyState;
    private NotificationAdapter adapter;
    private List<Notification> notificationList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.noti_user); // File xml bạn gửi

        initViews();
        setupRecyclerView();

        // Lấy UserID hiện tại (Giả sử bạn đã lưu khi Login)
        String currentUserId = getCurrentUserId();

        if (!currentUserId.isEmpty()) {
            loadNotifications(currentUserId);
        } else {
            Toast.makeText(this, "Lỗi: Không tìm thấy người dùng", Toast.LENGTH_SHORT).show();
        }
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar_notification);
        rvNotifications = findViewById(R.id.rv_notifications);
        layoutEmptyState = findViewById(R.id.layout_empty_state);

        // Xử lý nút Back chuẩn Material Toolbar
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        // Truyền logic hiển thị Dialog khi bấm vào
        adapter = new NotificationAdapter(notificationList, notification -> {
            showDetailDialog(notification);
        });
        rvNotifications.setLayoutManager(new LinearLayoutManager(this));
        rvNotifications.setAdapter(adapter);
    }
// NotificationUserActivity.java

    private void showDetailDialog(Notification noti) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle(noti.getTitle())
                .setMessage(noti.getBody() + "\n\nThời gian: " + noti.getCreatedAt())
                .setPositiveButton("Đóng", (dialog, which) -> {

                    // 1. Cập nhật giao diện ngay lập tức
                    noti.isRead = 1;
                    adapter.notifyDataSetChanged();

                    // 2. GỌI API ĐỂ CẬP NHẬT DATABASE
                    ApiService api = RetrofitClient.getClient().create(ApiService.class);
                    api.markSingleNotificationAsRead(noti.getNotiId()).enqueue(new Callback<BasicResponse>() {
                        @Override
                        public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                            if (response.isSuccessful()) {
                                android.util.Log.d("API_NOTI", "Database đã cập nhật IS_READ=1 cho " + noti.getNotiId());
                            }
                        }

                        @Override
                        public void onFailure(Call<BasicResponse> call, Throwable t) {
                            android.util.Log.e("API_NOTI", "Lỗi cập nhật Database");
                        }
                    });
                })
                .show();
    }

    private void loadNotifications(String userId) {
        ApiService api = RetrofitClient.getClient().create(ApiService.class);

        api.getNotifications(userId).enqueue(new Callback<List<Notification>>() {
            @Override
            public void onResponse(Call<List<Notification>> call, Response<List<Notification>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Notification> data = response.body();

                    if (data.isEmpty()) {
                        // Nếu không có thông báo -> Hiện màn hình trống
                        rvNotifications.setVisibility(View.GONE);
                        layoutEmptyState.setVisibility(View.VISIBLE);
                    } else {
                        // Có thông báo -> Hiện list
                        rvNotifications.setVisibility(View.VISIBLE);
                        layoutEmptyState.setVisibility(View.GONE);

                        notificationList.clear();
                        notificationList.addAll(data);
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(NotificationUserActivity.this, "Lỗi tải thông báo", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Notification>> call, Throwable t) {
                Toast.makeText(NotificationUserActivity.this, "Lỗi kết nối server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Hàm lấy UserID từ SharedPreferences (Bạn cần điều chỉnh key cho đúng với lúc Login)
    private String getCurrentUserId() {
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);

        String id = prefs.getString("user_id", "");

        if (id.isEmpty()) {
            String token = prefs.getString("access_token", "");
            android.util.Log.e("DEBUG_ID", "Token: " + token + " | ID: " + id);
        }

        android.util.Log.d("DEBUG_ID", "ID đang đăng nhập là: " + id);
        return id;
    }
}