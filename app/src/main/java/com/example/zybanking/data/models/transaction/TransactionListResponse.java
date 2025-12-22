package com.example.zybanking.data.models.transaction;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class TransactionListResponse {
    @SerializedName("status")
    private String status;
    @SerializedName("data")
    private List<Transaction> data;

    public String getStatus() {
        return status;
    }

    public List<Transaction> getData() {
        return data;
    }
}