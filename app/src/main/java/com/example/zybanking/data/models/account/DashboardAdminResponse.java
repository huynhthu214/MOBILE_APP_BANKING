package com.example.zybanking.data.models.account;

import com.example.zybanking.data.models.transaction.Transaction;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DashboardAdminResponse {
    @SerializedName("total_users")
    private int totalUsers;
    @SerializedName("total_transactions")
    private int totalTransactions;
    @SerializedName("total_amount")
    private double totalAmount;
    @SerializedName("recent_transactions")
    private List<Transaction> recentTransactions;

    // Getters...
}