package com.example.zybanking.data.models.account;

import com.google.gson.annotations.SerializedName;

public class Account {
    @SerializedName("ACCOUNT_ID") private String accountId;
    @SerializedName("ACCOUNT_TYPE") private String accountType;
    @SerializedName("BALANCE") private double balance;
    @SerializedName("ACCOUNT_NUMBER") private String accountNumber;

    // Getters
    public String getAccountType() { return accountType; }
    public String getAccountNumber() { return accountNumber; }
    public double getBalance() { return balance; }
}