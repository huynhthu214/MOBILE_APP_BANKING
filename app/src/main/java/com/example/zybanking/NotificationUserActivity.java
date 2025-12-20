package com.example.zybanking;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
        adapter = new NotificationAdapter(notificationList);
        rvNotifications.setLayoutManager(new LinearLayoutManager(this));
        rvNotifications.setAdapter(adapter);
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
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        return prefs.getString("user_id", "U001"); // Đổi từ USER01 thành U001
    }
}