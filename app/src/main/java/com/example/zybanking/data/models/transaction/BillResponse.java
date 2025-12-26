package com.example.zybanking.data.models.transaction;

public class BillResponse {
    public String bill_id;
    public String account_id;
    public String provider;
    public String customer_code;
    public int amount;
    public String status;
    public String created_at;
    public Bill data;
    public String message;
}
