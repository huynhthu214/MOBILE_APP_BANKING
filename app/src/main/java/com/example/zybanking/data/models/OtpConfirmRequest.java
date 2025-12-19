package com.example.zybanking.data.models;

public class OtpConfirmRequest {
    public String transaction_id;
    public String otp;

    public OtpConfirmRequest(String transaction_id, String otp) {
        this.transaction_id = transaction_id;
        this.otp = otp;
    }
}
