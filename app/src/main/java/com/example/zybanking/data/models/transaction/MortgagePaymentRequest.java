package com.example.zybanking.data.models.transaction;

import com.google.gson.annotations.SerializedName;

public class MortgagePaymentRequest {

    @SerializedName("mortgage_id")
    private String mortgageId;

    @SerializedName("amount")
    private double amount;

    @SerializedName("account_id")
    private String accountId;

    // Constructor
    public MortgagePaymentRequest(String mortgageId, double amount, String accountId) {
        this.mortgageId = mortgageId;
        this.amount = amount;
        this.accountId = accountId;
    }

    // Getter và Setter (Cần thiết cho một số thư viện hoặc khi debug)
    public String getMortgageId() {
        return mortgageId;
    }

    public void setMortgageId(String mortgageId) {
        this.mortgageId = mortgageId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
}