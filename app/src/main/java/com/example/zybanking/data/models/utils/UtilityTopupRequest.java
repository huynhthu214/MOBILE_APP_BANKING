package com.example.zybanking.data.models.utils;

public class UtilityTopupRequest {
    public String account_id;
    public String provider;
    public String phone_number;
    public int amount;

    public UtilityTopupRequest(String account_id, String provider,
                               String phone_number, int amount) {
        this.account_id = account_id;
        this.provider = provider;
        this.phone_number = phone_number;
        this.amount = amount;
    }
}

