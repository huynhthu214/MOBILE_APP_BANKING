package com.example.zybanking.data.models;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    public String status;
    public Data data;
    public String message;
    public static class Data {
        public String access_token;
        public String refresh_token;
        public User user;
    }

    public static class User {
        // Ánh xạ cả trường hợp server trả về "USER_ID" hoặc "user_id"
        @SerializedName(value = "USER_ID", alternate = {"user_id", "userId"})
        public String USER_ID;

        @SerializedName(value = "EMAIL", alternate = {"email"})
        public String EMAIL;

        @SerializedName(value = "FULL_NAME", alternate = {"full_name", "fullName"})
        public String FULL_NAME;

        // QUAN TRỌNG NHẤT LÀ DÒNG NÀY
        @SerializedName(value = "ROLE", alternate = {"role"})
        public String ROLE;
    }
}
