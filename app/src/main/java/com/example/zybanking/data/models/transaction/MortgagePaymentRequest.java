package com.example.zybanking.data.models.transaction;

import com.google.gson.annotations.SerializedName;

public class MortgagePaymentRequest {
    @SerializedName("account_id")
    private String accountId; // ID khoản vay (M004)

    @SerializedName("amount")
    private Double amount;

    public MortgagePaymentRequest(String accountId, Double amount) {
        this.accountId = accountId;
        this.amount = amount;
    }

    // Getter & Setter (nếu cần, nhưng constructor là đủ dùng)
}