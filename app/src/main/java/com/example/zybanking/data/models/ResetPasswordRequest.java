package com.example.zybanking.data.models;

public class ResetPasswordRequest {
    private String USER_ID;
    private String otp_code;
    private String new_password;

    public ResetPasswordRequest(String USER_ID, String otp_code, String new_password) {
        this.USER_ID = USER_ID;
        this.otp_code = otp_code;
        this.new_password = new_password;
    }
}