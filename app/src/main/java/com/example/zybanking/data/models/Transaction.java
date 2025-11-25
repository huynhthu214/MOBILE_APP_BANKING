package com.example.zybanking.data.models;

public class Transaction {
    private String id;
    private String accountId;
    private String description;
    private double amount;
    private String dateTime;

    public Transaction() {}

    public Transaction(String id, String accountId, String description, double amount, String dateTime) {
        this.id = id;
        this.accountId = accountId;
        this.description = description;
        this.amount = amount;
        this.dateTime = dateTime;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getDateTime() { return dateTime; }
    public void setDateTime(String dateTime) { this.dateTime = dateTime; }
}
