package com.example.zybanking.data.models.transaction;

public class WithdrawRequest {
    public String account_id;
    public Double amount;

    public WithdrawRequest(String account_id, Double amount) {
        this.account_id = account_id;
        this.amount = amount;
    }
    public WithdrawRequest() {
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
