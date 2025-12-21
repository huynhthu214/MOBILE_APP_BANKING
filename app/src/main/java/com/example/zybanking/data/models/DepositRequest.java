package com.example.zybanking.data.models;

import com.google.gson.annotations.SerializedName;

public class DepositRequest {
    @SerializedName("account_id")
    private String accountId;

    @SerializedName("amount")
    private double amount;

    public DepositRequest(String accountId, double amount) {
        this.accountId = accountId;
        this.amount = amount;
    }
}
