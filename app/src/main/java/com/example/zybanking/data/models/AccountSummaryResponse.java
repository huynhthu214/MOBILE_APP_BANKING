package com.example.zybanking.data.models;

import com.example.zybanking.data.models.Transaction;

import java.util.List;

public class AccountSummaryResponse {
    private String status;
    private String type;
    private double balance;
    private List<Transaction> last_transactions;

    public double getBalance() { return balance; }
    public List<Transaction> getLastTransactions() { return last_transactions; }
}