package com.example.zybanking.data.models.account;

import com.google.gson.annotations.SerializedName;

public class Account {
    @SerializedName("ACCOUNT_ID") private String accountId;
    @SerializedName("ACCOUNT_TYPE") private String accountType;
    @SerializedName("BALANCE") private double balance;
    @SerializedName("ACCOUNT_NUMBER") private String accountNumber;
    @SerializedName("STATUS") private String status;
    @SerializedName("USER_ID") private String userId;
    @SerializedName("FULL_NAME") private String ownerName;

    // Getters
    public String getOwnerName() { return ownerName; }
    public String getAccountId() { return accountId; }
    public String getAccountType() { return accountType; }
    public String getAccountNumber() { return accountNumber; }
    public double getBalance() { return balance; }
    public String getStatus() { return status; }

    // Setters cần thiết cho việc cập nhật thủ công nếu có
    public void setAccountNumber(String num) { this.accountNumber = num; }
    public void setAccountType(String type) { this.accountType = type; }
    public void setBalance(double balance) { this.balance = balance; }
}