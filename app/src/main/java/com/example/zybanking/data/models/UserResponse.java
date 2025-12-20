package com.example.zybanking.data.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class UserResponse {
    private String status;
    private Data data;

    public Data getData() { return data; }

    public static class Data {
        private User user;
        @SerializedName("ekyc")
        private Ekyc ekyc;
        private List<Map<String, Object>> accounts;

        public User getUser() { return user; }
        public Ekyc getEkyc() { return ekyc; }
        public List<Map<String, Object>> getAccounts() { return accounts; }
    }

    public class User {
        private String USER_ID;
        private String FULL_NAME;
        private String EMAIL;
        private String PHONE;
        private String ROLE;

        public String getFullName() { return FULL_NAME; }
        public String getEmail() { return EMAIL; }
        public String getUserId() { return USER_ID; }

        public String getPhone() { return PHONE; }

        public String getRole() { return ROLE; }
    }
    public static class Ekyc {
        @SerializedName("EKYC_ID")
        private String ekycId;

        @SerializedName("STATUS")
        private String status; // 'approved', 'pending', 'rejected'

        public String getStatus() { return status; }
    }
}