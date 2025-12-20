package com.example.zybanking.data.models.auth;

public class ForgotPasswordResponse {
    public String status;
    public String message;
    public Data data;

    public static class Data {
        public String otp_id;
        public String otp_code_dev;
        public String user_id;
    }
}