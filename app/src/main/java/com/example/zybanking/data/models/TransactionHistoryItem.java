package com.example.zybanking.data.models;

public class TransactionHistoryItem {
    public String id;        // Cần ID để khi click vào item sẽ gọi API lấy chi tiết
    public String title;
    public String time;
    public double amount;
    public boolean isIncome;

    public TransactionHistoryItem(String id, String title, String time, double amount, boolean isIncome) {
        this.id = id;
        this.title = title;
        this.time = time;
        this.amount = amount;
        this.isIncome = isIncome;
    }
}