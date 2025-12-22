package com.example.zybanking.ui.dashboard;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

// Import R để lấy ID layout
import com.example.zybanking.NavbarActivity;
import com.example.zybanking.R;
// Import Adapter và Models
import com.example.zybanking.data.adapter.TransactionAdapter;
import com.example.zybanking.data.models.TransactionHistoryResponse;
import com.example.zybanking.data.remote.ApiService;
import com.example.zybanking.data.remote.RetrofitClient;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryActivity extends NavbarActivity {

    private RecyclerView recyclerView;
    private TransactionAdapter adapter;
    private String accountId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.basic_history);

        // 2. KHỞI TẠO NAVBAR (Quan trọng nhất)
        initNavbar();

        // --- LẤY ACCOUNT_ID THẬT ---
        // Ưu tiên lấy từ Intent (nếu chuyển từ Home sang), nếu không có thì lấy trong SharedPrefs
        accountId = getIntent().getStringExtra("ACCOUNT_ID");

        if (accountId == null || accountId.isEmpty()) {
            android.content.SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
            // "main_account_id" là key bạn đã lưu lúc Login hoặc lúc load User ở Home
            accountId = prefs.getString("main_account_id", "");
        }

        // Kiểm tra nếu vẫn không có ID thì báo lỗi
        if (accountId.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy thông tin tài khoản", Toast.LENGTH_SHORT).show();
            // finish(); // Có thể đóng trang nếu cần
        }

        // Setup RecyclerView
        recyclerView = findViewById(R.id.rv_history);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TransactionAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(adapter);

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        loadData(apiService, accountId);
    }

    private void loadData(ApiService apiService, String id) {
        apiService.getTransactionHistory(id, 1).enqueue(new Callback<TransactionHistoryResponse>() {
            @Override
            public void onResponse(Call<TransactionHistoryResponse> call, Response<TransactionHistoryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if ("success".equals(response.body().getStatus())) {
                        adapter.updateData(response.body().getData());
                    }
                }
            }

            @Override
            public void onFailure(Call<TransactionHistoryResponse> call, Throwable t) {
                Toast.makeText(HistoryActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }
}