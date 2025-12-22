package com.example.zybanking.ui.dashboard;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

// Import R để lấy ID layout
import com.example.zybanking.R;
// Import Adapter và Models
import com.example.zybanking.data.adapter.TransactionAdapter;
import com.example.zybanking.data.models.TransactionHistoryResponse;
import com.example.zybanking.data.remote.ApiService;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TransactionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.basic_history);

        // 1. Setup RecyclerView
        recyclerView = findViewById(R.id.rv_history);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // --- FIX 1: Khởi tạo Adapter đúng với Constructor mới ---
        // Chúng ta truyền "this" (Context) và một danh sách rỗng ban đầu (new ArrayList<>())
        adapter = new TransactionAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // 2. Setup Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:5000/api/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        // 3. Gọi API
        loadData(apiService);
    }

    private void loadData(ApiService apiService) {
        apiService.getTransactionHistory("A001", 1).enqueue(new Callback<TransactionHistoryResponse>() {
            @Override
            public void onResponse(Call<TransactionHistoryResponse> call, Response<TransactionHistoryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if ("success".equals(response.body().getStatus())) {

                        // --- FIX 2: Gọi đúng hàm updateData đã viết trong Adapter ---
                        adapter.updateData(response.body().getData());

                    } else {
                        Toast.makeText(HistoryActivity.this, "Không lấy được dữ liệu", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(HistoryActivity.this, "Lỗi server: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TransactionHistoryResponse> call, Throwable t) {
                Toast.makeText(HistoryActivity.this, "Mất kết nối server", Toast.LENGTH_SHORT).show();
                t.printStackTrace(); // Log lỗi ra Logcat để debug
            }
        });
    }
}