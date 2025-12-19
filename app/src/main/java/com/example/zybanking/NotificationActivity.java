package com.example.zybanking;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zybanking.data.adapter.NotificationAdapter;
import com.example.zybanking.data.models.Notification;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends HeaderAdmin {
    private RecyclerView rvNotifications;
    private NotificationAdapter adapter;
    private List<Notification> notificationList;
    private ImageView btnBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_noti);
        initHeader();
        initViews();
        setupRecyclerView();
    }
    private void initViews() {
        rvNotifications = findViewById(R.id.rv_notifications);

        // Xử lý nút "Đánh dấu đã đọc"
        View btnMarkRead = findViewById(R.id.btn_mark_all_read);
        if (btnMarkRead != null) {
            btnMarkRead.setOnClickListener(v -> {
                Toast.makeText(this, "Đã đánh dấu tất cả là đã đọc!", Toast.LENGTH_SHORT).show();
                // Logic cập nhật UI (ẩn hết chấm đỏ) ở đây nếu muốn
            });
        }
    }

    private void setupRecyclerView() {
        // 1. Lấy dữ liệu giả
        notificationList = getMockData();

        // 2. Setup Adapter
        adapter = new NotificationAdapter(this, notificationList);
        rvNotifications.setLayoutManager(new LinearLayoutManager(this));
        rvNotifications.setAdapter(adapter);
    }

    // ==========================================
    // HÀM TẠO MOCK DATA (DỮ LIỆU GIẢ)
    // ==========================================
    private List<Notification> getMockData() {
        List<Notification> list = new ArrayList<>();

        // TYPE 1: GIAO DỊCH (Transaction)
        // TYPE 2: HỆ THỐNG (System)
        // TYPE 3: NGƯỜI DÙNG (User)

        // 1. Thông báo chưa đọc (isRead = false)
        list.add(new Notification(1,
                "Giao dịch giá trị lớn bất thường",
                "Tài khoản 098****123 vừa thực hiện chuyển đi 500,000,000 VND. Vui lòng kiểm tra.",
                "2 phút trước",
                NotificationAdapter.TYPE_TRANSACTION,
                false));

        list.add(new Notification(2,
                "Yêu cầu mở khóa tài khoản",
                "Khách hàng Nguyễn Văn A yêu cầu mở khóa do nhập sai mật khẩu quá 5 lần.",
                "15 phút trước",
                NotificationAdapter.TYPE_USER,
                false));

        list.add(new Notification(3,
                "Hệ thống bảo trì định kỳ",
                "Hệ thống sẽ tạm dừng để bảo trì từ 00:00 - 02:00 ngày 20/12/2025.",
                "1 giờ trước",
                NotificationAdapter.TYPE_SYSTEM,
                false));

        // 2. Thông báo đã đọc (isRead = true)
        list.add(new Notification(4,
                "Xác thực eKYC thành công",
                "Hồ sơ eKYC của khách hàng Trần Thị B đã được hệ thống duyệt tự động.",
                "3 giờ trước",
                NotificationAdapter.TYPE_USER,
                true));

        list.add(new Notification(5,
                "Cảnh báo đăng nhập lạ",
                "Phát hiện đăng nhập từ IP nước ngoài vào tài khoản Admin lúc 10:30.",
                "1 ngày trước",
                NotificationAdapter.TYPE_SYSTEM,
                true));

        list.add(new Notification(6,
                "Biến động số dư quỹ",
                "Tổng quỹ tín dụng tăng +2,000,000,000 VND sau kỳ đáo hạn.",
                "1 ngày trước",
                NotificationAdapter.TYPE_TRANSACTION,
                true));

        list.add(new Notification(7,
                "Yêu cầu cấp lại mật khẩu",
                "Khách hàng Lê Văn C yêu cầu cấp lại mật khẩu qua Email.",
                "2 ngày trước",
                NotificationAdapter.TYPE_USER,
                true));

        return list;
    }
}
