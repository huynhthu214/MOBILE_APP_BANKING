package com.example.zybanking.data.models.auth;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    public String status;
    public Data data;
    public String message;

    public String getStatus() { return status; }
    public Data getData() { return data; }

    public static class Data {
        public String access_token;
        public String refresh_token;
        public User user;

        public String getToken() { return access_token; }
    }

    public static class User {
        @SerializedName(value = "USER_ID", alternate = {"user_id", "userId"})
        public String USER_ID;

        @SerializedName(value = "EMAIL", alternate = {"email"})
        public String EMAIL;

        @SerializedName(value = "FULL_NAME", alternate = {"full_name", "fullName"})
        public String FULL_NAME;

        @SerializedName(value = "ROLE", alternate = {"role"})
        public String ROLE;

        // --- THÊM DÒNG NÀY ---
        @SerializedName(value = "ACCOUNT_ID", alternate = {"account_id", "accountId"})
        public String ACCOUNT_ID;
    }
}