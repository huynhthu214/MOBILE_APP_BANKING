package com.example.zybanking.ui.transaction;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zybanking.R;
import com.example.zybanking.data.adapter.TransactionAdapter;
import com.example.zybanking.data.models.TransactionHistoryResponse;
import com.example.zybanking.data.models.transaction.Transaction; // Import đúng model Transaction
import com.example.zybanking.data.repository.TransactionRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView rvHistory;
    private TransactionRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.basic_history);

        rvHistory = findViewById(R.id.rv_history);
        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        rvHistory.setHasFixedSize(true);

        repository = new TransactionRepository();

        loadTransactionHistory();
    }

    private void loadTransactionHistory() {
        // ===== LẤY account_id TỪ SESSION =====
        SharedPreferences pref = getSharedPreferences("auth", MODE_PRIVATE);
        String accountId = pref.getString("account_id", "");
        Log.d("HISTORY", "accountId = " + accountId);

        if (accountId == null || accountId.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy tài khoản", Toast.LENGTH_SHORT).show();
            return;
        }

        repository.getTransactionHistory(
                accountId,
                HistoryActivity.this,
                new Callback<TransactionHistoryResponse>() {
                    @Override
                    public void onResponse(Call<TransactionHistoryResponse> call, Response<TransactionHistoryResponse> response) {
                        if (!response.isSuccessful()) {
                            Toast.makeText(HistoryActivity.this, "HTTP lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        TransactionHistoryResponse body = response.body();

                        if (body == null || body.getData() == null) {
                            Toast.makeText(HistoryActivity.this, "Không có dữ liệu", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if ("success".equalsIgnoreCase(body.getStatus())) {
                            // --- SỬA Ở ĐÂY ---
                            // Không cần mapTransactions nữa.
                            // Truyền trực tiếp List<Transaction> vào Adapter
                            List<Transaction> listTransaction = body.getData();

                            TransactionAdapter adapter = new TransactionAdapter(
                                    HistoryActivity.this,
                                    listTransaction
                            );
                            rvHistory.setAdapter(adapter);
                            // -----------------

                        } else {
                            Toast.makeText(HistoryActivity.this, "Lỗi tải lịch sử", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<TransactionHistoryResponse> call, Throwable t) {
                        Toast.makeText(HistoryActivity.this, "Không kết nối được backend", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

}