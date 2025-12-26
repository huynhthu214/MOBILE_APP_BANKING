package com.example.zybanking.ui.utilities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zybanking.NavbarActivity; // Giả sử bạn kế thừa NavbarActivity như mẫu
import com.example.zybanking.R;
import com.example.zybanking.data.adapter.RecentBillAdapter;
import com.example.zybanking.data.models.transaction.Transaction;
import com.example.zybanking.data.models.transaction.TransactionHistoryResponse;
import com.example.zybanking.data.remote.ApiService;
import com.example.zybanking.data.remote.RetrofitClient;
// Bạn cần tạo Adapter: TransactionAdapter để hiển thị list
// import com.example.zybanking.ui.adapter.TransactionAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UtilitiesActivity extends NavbarActivity {

    private LinearLayout btnBack;
    private RecyclerView rvRecentBills;
    private TextView tvEmptyHistory;
    private CardView btnElectricity, btnWater, btnPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.basic_uti);

        initViews();
        setupEvents();
        loadTransactionHistory();

        // Gọi initNavbar từ NavbarActivity cha
        initNavbar();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back_uti);
        btnElectricity = findViewById(R.id.btn_electricity);
        btnWater = findViewById(R.id.btn_water);
        btnPhone = findViewById(R.id.btn_phone_topup);

        // Giả sử bạn đã thêm RecyclerView vào layout basic_uti.xml với id rv_history
        rvRecentBills = findViewById(R.id.rv_recent_bills);
        tvEmptyHistory = findViewById(R.id.tv_empty_history);

        rvRecentBills.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupEvents() {
        btnBack.setOnClickListener(v -> finish());

        // Chuyển sang màn hình thanh toán Điện/Nước
        btnElectricity.setOnClickListener(v -> {
            Intent intent = new Intent(this, ElectricWaterPayment.class);
            startActivity(intent);
        });

        btnWater.setOnClickListener(v -> {
            Intent intent = new Intent(this, ElectricWaterPayment.class);
            startActivity(intent);
        });

        btnPhone.setOnClickListener(v -> {
            Intent intent = new Intent(this, PhonePayment.class);
            startActivity(intent);
        });
    }

    private void loadTransactionHistory() {
        SharedPreferences pref = getSharedPreferences("auth", MODE_PRIVATE);
        String accountId = pref.getString("ACCOUNT_ID", "");

        if (accountId.isEmpty()) return;

        ApiService api = RetrofitClient.getClient().create(ApiService.class);

        // Sử dụng API lấy lịch sử giao dịch [cite: 306]
        api.getTransactionHistory(accountId, 1).enqueue(new Callback<TransactionHistoryResponse>() {
            @Override
            public void onResponse(Call<TransactionHistoryResponse> call, Response<TransactionHistoryResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                    List<Transaction> allTransactions = response.body().data;
                    List<Transaction> utilityTransactions = new ArrayList<>();

                    // Lọc: Chỉ lấy các giao dịch có type là 'utility' [cite: 153]
                    for (Transaction tx : allTransactions) {
                        if ("utility".equalsIgnoreCase(tx.getType())) {
                            utilityTransactions.add(tx);
                        }
                    }

                    if (utilityTransactions.isEmpty()) {
                        rvRecentBills.setVisibility(View.GONE);
                        tvEmptyHistory.setVisibility(View.VISIBLE);
                    } else {
                        rvRecentBills.setVisibility(View.VISIBLE);
                        tvEmptyHistory.setVisibility(View.GONE);

                        // Gán Adapter
                        RecentBillAdapter adapter = new RecentBillAdapter(utilityTransactions);
                        rvRecentBills.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onFailure(Call<TransactionHistoryResponse> call, Throwable t) {
                tvEmptyHistory.setText("Lỗi kết nối server");
                tvEmptyHistory.setVisibility(View.VISIBLE);
            }
        });
    }
}