package com.example.zybanking.data.models;

public class ResetPasswordRequest {
    private String user_id;
    private String otp_code;
    private String new_password;

    public ResetPasswordRequest(String user_id, String otp_code, String new_password) {
        this.user_id = user_id;
        this.otp_code = otp_code;
        this.new_password = new_password;
    }
}