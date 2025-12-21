package com.example.zybanking.data.models;

public class TransactionHistoryItem {

    public String title;     // VD: "Nạp tiền", "Chuyển tiền đến A"
    public String time;      // VD: "19/12/2025 - 14:30"
    public double amount;    // 500000
    public boolean isIncome; // true = +, false = -

    public TransactionHistoryItem(
            String title,
            String time,
            double amount,
            boolean isIncome
    ) {
        this.title = title;
        this.time = time;
        this.amount = amount;
        this.isIncome = isIncome;
    }
}
