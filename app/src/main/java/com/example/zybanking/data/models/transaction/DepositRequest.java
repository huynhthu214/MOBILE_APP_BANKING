package com.example.zybanking.data.models.transaction;

public class DepositRequest {
    public String account_id;
    public Double amount;

    public DepositRequest(String account_id, Double amount) {
        this.account_id = account_id;
        this.amount = amount;
    }
    public String getAccountId() {
        return account_id;
    }
    public DepositRequest() {
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
