package com.example.zybanking;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.zybanking.data.adapter.NotificationAdapter;
import com.example.zybanking.data.models.BasicResponse;
import com.example.zybanking.data.models.Notification;
import com.example.zybanking.data.remote.ApiService;
import com.example.zybanking.data.remote.RetrofitClient;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationAdminActivity extends HeaderAdmin {

    private RecyclerView rvNotifications;
    private NotificationAdapter adapter;
    private List<Notification> fullList = new ArrayList<>();     // Danh sách gốc từ Server
    private List<Notification> displayList = new ArrayList<>();  // Danh sách đang hiển thị (đã lọc)
    private TextView tabAll, tabSystem, tabTransaction, tabUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_noti);

        initHeader();
        initViews();
        setupRecyclerView();
        setupTabListeners();
        setupMarkAllRead();
        loadNotifications();
    }

    private void initViews() {
        rvNotifications = findViewById(R.id.rv_notifications);
        tabAll = findViewById(R.id.tab_all);
        tabSystem = findViewById(R.id.tab_system);
        tabTransaction = findViewById(R.id.tab_transaction);
        tabUsers = findViewById(R.id.tab_users);
    }

    private void setupRecyclerView() {
        adapter = new NotificationAdapter(displayList, notification -> {
            notification.isRead = 1;
            adapter.notifyDataSetChanged();

            ApiService api = RetrofitClient.getClient().create(ApiService.class);
            api.markSingleNotificationAsRead(notification.getNotiId()).enqueue(new Callback<BasicResponse>() {
                @Override
                public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                    if (response.isSuccessful()) {
                        android.util.Log.d("API_DEBUG", "Đã cập nhật IS_READ=1 cho thông báo: " + notification.getNotiId());
                    }
                }

                @Override
                public void onFailure(Call<BasicResponse> call, Throwable t) {
                    android.util.Log.e("API_DEBUG", "Lỗi cập nhật database: " + t.getMessage());
                }
            });

            // 3. Hiển thị Dialog chi tiết
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle(notification.getTitle())
                    .setMessage(notification.getBody())
                    .setPositiveButton("Đóng", null)
                    .show();
        });
        rvNotifications.setLayoutManager(new LinearLayoutManager(this));
        rvNotifications.setAdapter(adapter);
    }

    private void setupTabListeners() {
        tabAll.setOnClickListener(v -> filterNotifications("ALL"));
        tabSystem.setOnClickListener(v -> filterNotifications("SYSTEM"));
        tabTransaction.setOnClickListener(v -> filterNotifications("TRANSACTION"));
        tabUsers.setOnClickListener(v -> filterNotifications("BILL")); // Thường là nhắc nợ người dùng
    }

    private void filterNotifications(String type) {
        updateTabUI(type);
        displayList.clear();

        if (type.equals("ALL")) {
            displayList.addAll(fullList);
        } else {
            // Lọc danh sách gốc dựa trên trường TYPE từ Database
            for (Notification n : fullList) {
                if (n.getType() != null && n.getType().equals(type)) {
                    displayList.add(n);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void updateTabUI(String type) {
        // Reset tất cả về màu xám
        int grayText = Color.parseColor("#4B5563");
        tabAll.setBackgroundResource(R.drawable.bg_chip_outline); tabAll.setTextColor(grayText);
        tabSystem.setBackgroundResource(R.drawable.bg_chip_outline); tabSystem.setTextColor(grayText);
        tabTransaction.setBackgroundResource(R.drawable.bg_chip_outline); tabTransaction.setTextColor(grayText);
        tabUsers.setBackgroundResource(R.drawable.bg_chip_outline); tabUsers.setTextColor(grayText);

        // Highlight tab được chọn
        TextView selected = tabAll;
        if (type.equals("SYSTEM")) selected = tabSystem;
        else if (type.equals("TRANSACTION")) selected = tabTransaction;
        else if (type.equals("BILL")) selected = tabUsers;

        selected.setBackgroundResource(R.drawable.bg_chip_filled);
        selected.setTextColor(Color.WHITE);
    }
    private void setupMarkAllRead() {
        TextView btnMarkRead = findViewById(R.id.btn_mark_all_read);
        btnMarkRead.setOnClickListener(v -> {
            // --- XỬ LÝ ĐỌC TẤT CẢ (READ ALL) ---

            SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
            String currentUserId = prefs.getString("user_id", "");

            if (currentUserId.isEmpty()) return;

            ApiService api = RetrofitClient.getClient().create(ApiService.class);
            // Gọi API PUT /api/v1/notifications/{userId}/read
            api.markNotificationsAsRead(currentUserId).enqueue(new Callback<BasicResponse>() {
                @Override
                public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                    if (response.isSuccessful()) {
                        // Cập nhật toàn bộ danh sách trong bộ nhớ
                        for (Notification n : fullList) {
                            n.isRead = 1;
                        }
                        adapter.notifyDataSetChanged();
                        Toast.makeText(NotificationAdminActivity.this, "Đã đọc tất cả thông báo", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<BasicResponse> call, Throwable t) {
                    Toast.makeText(NotificationAdminActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
    private void loadNotifications() {
        // 1. Lấy ID từ bộ nhớ (phải khớp với key "user_id" lúc bạn lưu khi Login)
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        String adminId = prefs.getString("user_id", "");

        if (adminId.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy ID Admin", Toast.LENGTH_SHORT).show();
            return;
        }
        ApiService api = RetrofitClient.getClient().create(ApiService.class);
        api.getNotifications(adminId).enqueue(new Callback<List<Notification>>() {
            @Override
            public void onResponse(Call<List<Notification>> call, Response<List<Notification>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    fullList.clear();
                    fullList.addAll(response.body());
                    filterNotifications("ALL");
                }
            }
            @Override
            public void onFailure(Call<List<Notification>> call, Throwable t) {
                Toast.makeText(NotificationAdminActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }
}