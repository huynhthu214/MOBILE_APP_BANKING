package com.example.zybanking.data.models;

public class DepositRequest {
    public String account_id;
    public float amount;

    public DepositRequest(String account_id, float amount) {
        this.account_id = account_id;
        this.amount = amount;
    }
}
