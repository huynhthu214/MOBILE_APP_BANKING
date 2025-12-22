package com.example.zybanking.data.models.transaction;

import com.google.gson.annotations.SerializedName;

public class Transaction {
    @SerializedName("TRANSACTION_ID")
    private String transactionId;

    @SerializedName("AMOUNT")
    private double amount;

    @SerializedName("STATUS")
    private String status;

    @SerializedName("CREATED_AT")
    private String createdAt;

    @SerializedName("DEST_ACC_NAME")
    private String destName;

    @SerializedName("TYPE")
    private String type;

    // Getters
    public String getTransactionId() { return transactionId; }
    public double getAmount() { return amount; }
    public String getStatus() { return status; }
    public String getCreatedAt() { return createdAt; }
    public String getDestName() { return destName; }
    public String getType() { return type; }
}