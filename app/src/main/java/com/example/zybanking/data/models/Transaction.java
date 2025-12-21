package com.example.zybanking.data.models;
public class Transaction {
    private String transaction_id;
    private String type;
    private double amount;
    private String status;
    private String date;

    public String getType() { return type; }
    public double getAmount() { return amount; }
    public String getStatus() { return status; }
    public String getDate() { return date; }
}
