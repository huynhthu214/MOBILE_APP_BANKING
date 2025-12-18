package com.example.zybanking.data.models;

public class ForgotPasswordResponse {
    public String status;
    public String message;
    public Data data;

    public static class Data {
        public String otp_id;
        public String otp_code_dev;
        public String USER_ID; // App cần cái này để reset pass
    }
}