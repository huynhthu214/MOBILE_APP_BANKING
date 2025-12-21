package com.example.zybanking.data.repository;

import android.content.Context;
import android.util.Log;

import com.example.zybanking.data.models.BasicResponse;
import com.example.zybanking.data.models.transaction.DepositRequest;
import com.example.zybanking.data.models.TransactionHistoryResponse;
import com.example.zybanking.data.remote.ApiService;
import com.example.zybanking.data.remote.RetrofitClient;
import com.example.zybanking.utils.TokenManager;

import retrofit2.Call;
import retrofit2.Callback;

public class TransactionRepository {

    private ApiService apiService;

    public TransactionRepository() {
        apiService = RetrofitClient
                .getClient()
                .create(ApiService.class);
    }

    // ================== DEPOSIT ==================
    public void createDeposit(
            Context context,
            DepositRequest request,
            Callback<BasicResponse> callback
    ) {
        String token = "Bearer " + TokenManager.getAccessToken(context);
        apiService.deposit(token, request).enqueue(callback);
    }
    public void getTransactionHistory(
            String accountId,
            Context context,
            Callback<TransactionHistoryResponse> callback
    ) {
        apiService.getTransactionHistory(accountId).enqueue(callback);
    }
}
