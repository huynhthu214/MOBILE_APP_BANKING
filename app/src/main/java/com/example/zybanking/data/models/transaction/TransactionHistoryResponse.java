package com.example.zybanking.data.models.transaction;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class TransactionHistoryResponse {

    @SerializedName("status")
    public String status;

    @SerializedName("message")
    public String message;

    @SerializedName("data")
    public List<Transaction> data;

    // Constructor, Getters và Setters
    public TransactionHistoryResponse() {}

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public List<Transaction> getData() { return data; }
    public void setData(List<Transaction> data) { this.data = data; }
}