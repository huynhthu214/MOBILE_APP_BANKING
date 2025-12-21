package com.example.zybanking.ui.transaction;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zybanking.HeaderAdmin;
import com.example.zybanking.R;
import com.example.zybanking.data.models.transaction.Transaction;
import com.example.zybanking.data.models.transaction.TransactionListResponse; // Create this model
import com.example.zybanking.data.remote.ApiService;
import com.example.zybanking.data.remote.RetrofitClient;
import com.example.zybanking.data.adapter.AdminTransactionAdapter; // Create this adapter

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminTransaction extends HeaderAdmin {
    private RecyclerView rvTransactions;
    private AdminTransactionAdapter adapter;
    private List<Transaction> transactionList = new ArrayList<>();
    private ApiService apiService;
    private String token;
    private String currentStatus = ""; // "" = ALL

    private CardView tabAll, tabSuccess, tabFailed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_transactions);
        initHeader();

        apiService = RetrofitClient.getClient().create(ApiService.class);
        loadToken();

        initViews();
        setupTabs();

        loadTransactions("");
    }

    private void loadToken() {
        SharedPreferences pref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String savedToken = pref.getString("auth_token", "");
        token = savedToken.startsWith("Bearer ") ? savedToken : "Bearer " + savedToken;
    }

    private void initViews() {
        rvTransactions = findViewById(R.id.rvTransactions);
        rvTransactions.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminTransactionAdapter(this, transactionList);
        rvTransactions.setAdapter(adapter);

        tabAll = findViewById(R.id.tabAllTrans);
        tabSuccess = findViewById(R.id.tabSuccess);
        tabFailed = findViewById(R.id.tabFailed);
    }

    private void setupTabs() {
        tabAll.setOnClickListener(v -> {
            updateTabColor(tabAll, tabSuccess, tabFailed);
            currentStatus = "";
            loadTransactions("");
        });

        tabSuccess.setOnClickListener(v -> {
            updateTabColor(tabSuccess, tabAll, tabFailed);
            currentStatus = "COMPLETED"; // Or "SUCCESS" based on your DB
            loadTransactions("");
        });

        tabFailed.setOnClickListener(v -> {
            updateTabColor(tabFailed, tabAll, tabSuccess);
            currentStatus = "FAILED";
            loadTransactions("");
        });
    }

    private void updateTabColor(CardView selected, CardView... others) {
        selected.setCardBackgroundColor(Color.parseColor("#2563EB")); // Blue
        // Set text color white logic here if needed via TextView reference

        for (CardView other : others) {
            other.setCardBackgroundColor(Color.parseColor("#F3F4F6")); // Gray
        }
    }

    private void loadTransactions(String search) {
        apiService.getAdminTransactions(token, search, currentStatus).enqueue(new Callback<TransactionListResponse>() {
            @Override
            public void onResponse(Call<TransactionListResponse> call, Response<TransactionListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    transactionList.clear();
                    transactionList.addAll(response.body().getData());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<TransactionListResponse> call, Throwable t) {
                Toast.makeText(AdminTransaction.this, "Failed to load transactions", Toast.LENGTH_SHORT).show();
            }
        });
    }
}