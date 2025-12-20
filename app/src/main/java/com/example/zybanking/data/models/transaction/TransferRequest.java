package com.example.zybanking.data.models.transaction;

public class TransferRequest {
    public String from_account_id;
    public String to_account_number;
    public int amount;

    public TransferRequest(String from, String to, int amount) {
        this.from_account_id = from;
        this.to_account_number = to;
        this.amount = amount;
    }
}
