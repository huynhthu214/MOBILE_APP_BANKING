package com.example.zybanking.data.models.transaction;

import com.example.zybanking.data.models.transaction.Transaction;

import java.util.List;

public class TransactionListResponse {
    private String status;
    private List<Transaction> data; // Biến này phải trùng tên key "data" trong JSON trả về

    public String getStatus() {
        return status;
    }

    public List<Transaction> getData() {
        return data;
    }
}