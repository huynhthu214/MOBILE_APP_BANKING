package com.example.zybanking.data.models;

public class Account {
    private String id;
    private String userId;
    private AccountType type;
    private double balance;
    private double interestRate; // only for saving
    private String dueDate; // only for mortgage

    public Account() {}

    public Account(String id, String userId, AccountType type, double balance, double interestRate, String dueDate) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.balance = balance;
        this.interestRate = interestRate;
        this.dueDate = dueDate;
    }

    // getter & setter
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public AccountType getType() { return type; }
    public void setType(AccountType type) { this.type = type; }
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
    public double getInterestRate() { return interestRate; }
    public void setInterestRate(double interestRate) { this.interestRate = interestRate; }
    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }
}
