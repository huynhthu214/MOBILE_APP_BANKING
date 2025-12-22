package com.example.zybanking.data.models.utility;

public class UtilityRequest {
    public String account_id;
    public String provider;
    public String phone_number; // Hoặc mã khách hàng đối với Điện/Nước
    public Double amount;

    public UtilityRequest(String account_id, String provider, String phone_number, Double amount) {
        this.account_id = account_id;
        this.provider = provider;
        this.phone_number = phone_number;
        this.amount = amount;
    }
}