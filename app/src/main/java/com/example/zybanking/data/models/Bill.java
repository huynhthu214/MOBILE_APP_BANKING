package com.example.zybanking.data.models;

public class Bill {
    private String id;
    private String accountId;
    private String type;
    private double amount;
    private boolean isPaid;

    public Bill() {}

    public Bill(String id, String accountId, String type, double amount, boolean isPaid) {
        this.id = id;
        this.accountId = accountId;
        this.type = type;
        this.amount = amount;
        this.isPaid = isPaid;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public boolean isPaid() { return isPaid; }
    public void setPaid(boolean paid) { isPaid = paid; }
}
