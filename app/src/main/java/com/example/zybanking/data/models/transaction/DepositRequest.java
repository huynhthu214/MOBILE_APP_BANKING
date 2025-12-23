package com.example.zybanking.data.models.transaction;

import com.google.gson.annotations.SerializedName;

public class DepositRequest {

    @SerializedName("account_id")
    public String accountId;

    @SerializedName("amount")
    public double amount;
    @SerializedName("pin")
    private String pin;
    @SerializedName("saving_acc_id")
    private String savingAccId;
    @SerializedName("payment_method")
    public String paymentMethod;

    public DepositRequest(String accountId, double amount, String paymentMethod) {
        this.accountId = accountId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
    }
    public DepositRequest(String accountId, String savingAccId, double amount, String pin) {
        this.accountId = accountId;
        this.savingAccId = savingAccId;
        this.amount = amount;
        this.pin = pin;
    }

}
