package com.example.zybanking.data.models.auth;

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

    public static class User {
        @SerializedName("USER_ID")
        private String USER_ID;

        @SerializedName("FULL_NAME") // Bắt buộc phải có để khớp với DB
        private String FULL_NAME;

        @SerializedName("EMAIL")      // Bắt buộc phải có
        private String EMAIL;

        @SerializedName("PHONE")
        private String PHONE;

        @SerializedName("ROLE")
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

        // QUAN TRỌNG NHẤT: Thêm dòng này để đọc được "STATUS": "pending" từ Server
        @SerializedName("STATUS")
        private String status;

        public String getStatus() { return status; }
    }
}