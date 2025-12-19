package com.example.zybanking.data.models;

public class WithdrawRequest {
    public String account_id;
    public int amount;

    public WithdrawRequest(String account_id, int amount) {
        this.account_id = account_id;
        this.amount = amount;
    }
}
