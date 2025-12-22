package com.example.zybanking.data.models.transaction;

public class WithdrawRequest {
    private String account_id;
    private double amount;
    private String currency;
    private String otp;

    public WithdrawRequest(String account_id, double amount) {
        this.account_id = account_id;
        this.amount = amount;
        this.currency = "VND";
    }
    public WithdrawRequest() {}
    public WithdrawRequest(String account_id, double amount, String otp) {
        this.account_id = account_id;
        this.amount = amount;
        this.otp = otp;
    }
    public String getAccountId() {
        return account_id;
    }

    public void setAccountId(String accountId) {
        this.account_id = accountId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
