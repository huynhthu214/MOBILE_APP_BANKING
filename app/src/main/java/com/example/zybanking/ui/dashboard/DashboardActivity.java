package com.example.zybanking.ui.dashboard;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView; // Import CardView

import com.example.zybanking.R;

public class DashboardActivity extends AppCompatActivity {

    // 1. Khai báo biến
    private TextView tvUserName, tvCurrentBalance;
    private CardView cardSearch, cardNotifications, cardAddBalance;

    // Các nút chức năng
    private LinearLayout btnLoan, btnSavings, btnMortgage;
    private LinearLayout btnFlightTickets, btnMovieTickets, btnBusTickets;
    private LinearLayout btnHotel, btnEcommerce;
    private LinearLayout btnBranchLocations, btnFaq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

        // 2. Ánh xạ View (Binding)
        initViews();

        // 3. Xử lý dữ liệu
        setupData();

        // 4. Bắt sự kiện Click
        setupListeners();
    }

    private void initViews() {
        // Header & Balance
        tvUserName = findViewById(R.id.tv_user_name);
        tvCurrentBalance = findViewById(R.id.tv_current_balance);
        cardSearch = findViewById(R.id.card_search);
        cardNotifications = findViewById(R.id.card_notifications);
        cardAddBalance = findViewById(R.id.cardAddBalance);

        // Tài khoản (Account Types)
        btnLoan = findViewById(R.id.btn_loan);
        btnSavings = findViewById(R.id.btn_savings);
        btnMortgage = findViewById(R.id.btn_mortgage);

        // Tiện ích (Utilities)
        btnFlightTickets = findViewById(R.id.btn_flight_tickets);
        btnMovieTickets = findViewById(R.id.btn_movie_tickets);
        btnBusTickets = findViewById(R.id.btn_bus_tickets);
        btnHotel = findViewById(R.id.btn_hotel);
        btnEcommerce = findViewById(R.id.btn_ecommerce);

        // Hỗ trợ (Support)
        btnBranchLocations = findViewById(R.id.btn_branch_locations);
        btnFaq = findViewById(R.id.btn_faq);
    }

    private void setupData() {
        // Nhận dữ liệu từ LoginActivity (nếu có)
        String userName = getIntent().getStringExtra("user_name");
        if (userName != null && !userName.isEmpty()) {
            tvUserName.setText(userName + "!");
        }

        // Dữ liệu fake demo
        tvCurrentBalance.setText("4.520.000 VND");
    }

    private void setupListeners() {
        // --- Header Actions ---
        cardSearch.setOnClickListener(v -> showToast("Tìm kiếm"));
        cardNotifications.setOnClickListener(v -> showToast("Thông báo"));
        cardAddBalance.setOnClickListener(v -> showToast("Nạp thêm tiền vào tài khoản"));

        // --- Account Actions ---
        btnLoan.setOnClickListener(v -> showToast("Vay tiền"));
        btnSavings.setOnClickListener(v -> showToast("Tiết kiệm"));
        btnMortgage.setOnClickListener(v -> showToast("Thế chấp"));

        // --- Utility Actions ---
        btnFlightTickets.setOnClickListener(v -> showToast("Đặt vé máy bay"));
        btnMovieTickets.setOnClickListener(v -> showToast("Đặt vé xem phim"));
        btnBusTickets.setOnClickListener(v -> showToast("Đặt vé xe"));
        btnHotel.setOnClickListener(v -> showToast("Đặt phòng khách sạn"));
        btnEcommerce.setOnClickListener(v -> showToast("Mua sắm Online"));

        // --- Support Actions ---
        btnBranchLocations.setOnClickListener(v -> showToast("Bản đồ chi nhánh"));
        btnFaq.setOnClickListener(v -> showToast("Câu hỏi thường gặp (FAQ)"));
    }

    private void showToast(String message) {
        Toast.makeText(this, message + " đang được phát triển", Toast.LENGTH_SHORT).show();
    }
}