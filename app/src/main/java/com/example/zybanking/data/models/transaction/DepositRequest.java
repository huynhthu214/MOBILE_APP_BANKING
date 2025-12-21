package com.example.zybanking.data.models.transaction;

import com.google.gson.annotations.SerializedName;

public class DepositRequest {

    // Backend Python bắt buộc phải thấy chữ "account_id" này
    @SerializedName("account_id")
    private String accountId;

    @SerializedName("amount")
    private double amount;

    // Constructor rỗng (để tránh lỗi)
    public DepositRequest() {}

    // Constructor có tham số
    public DepositRequest(String accountId, double amount) {
        this.accountId = accountId;
        this.amount = amount;
    }

    // Getter và Setter
    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}