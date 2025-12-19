package com.example.zybanking.data.models;

public class VerifyOtpRequest {
    private String user_id;
    private String otp_code;

    public VerifyOtpRequest(String user_id, String otpCode) {
        this.user_id = user_id;
        this.otp_code = otpCode;
    }
}
