package com.example.zybanking.data.models;

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
        public String USER_ID;
        public String EMAIL;
        public String FULL_NAME;
        public String ROLE;
    }
}
