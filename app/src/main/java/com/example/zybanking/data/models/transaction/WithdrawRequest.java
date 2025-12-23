package com.example.zybanking.data.models.transaction;

import com.google.gson.annotations.SerializedName;

public class WithdrawRequest {
    @SerializedName("account_id") // Ánh xạ sang account_id cho Flask
    public String accountId;

    @SerializedName("amount")
    public double amount;

    @SerializedName("pin")
    public String pin;

    @SerializedName("saving_acc_id")
    public String savingAccId;

    @SerializedName("payment_method")
    public String paymentMethod;

    // Constructor cho rút tiền thường (ATM)
    public WithdrawRequest(String accountId, double amount, String paymentMethod) {
        this.accountId = accountId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
    }

    // Constructor dùng cho rút tiền tiết kiệm (Savings Withdrawal)
    public WithdrawRequest(String accountId, String savingAccId, double amount, String pin) {
        this.accountId = accountId;
        this.savingAccId = savingAccId;
        this.amount = amount;
        this.pin = pin;
    }
}