package com.example.zybanking.data.models;

public class BillPayRequest {
    public String account_id;
    public String bill_id;

    public BillPayRequest(String account_id, String bill_id) {
        this.account_id = account_id;
        this.bill_id = bill_id;
    }
}

