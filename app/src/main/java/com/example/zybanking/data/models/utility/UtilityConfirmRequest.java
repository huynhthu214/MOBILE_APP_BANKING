package com.example.zybanking.data.models.utility;

public class UtilityConfirmRequest {
    public String transaction_id;
    public String otp;

    public UtilityConfirmRequest(String transaction_id, String otp) {
        this.transaction_id = transaction_id;
        this.otp = otp;
    }
}