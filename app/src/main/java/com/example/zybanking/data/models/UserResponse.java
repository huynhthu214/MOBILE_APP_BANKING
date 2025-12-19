package com.example.zybanking.data.models;

import java.util.List;
import java.util.Map;

public class UserResponse {
    private String status;
    private Data data;

    public Data getData() { return data; }

    public static class Data {
        private User user;
        private List<Map<String, Object>> accounts; // thêm dòng này

        public User getUser() { return user; }
        public List<Map<String, Object>> getAccounts() { return accounts; } // getter phải tồn tại
    }

    public class User {
        private String USER_ID;
        private String FULL_NAME;
        private String EMAIL;
        private String PHONE;

        public String getFullName() { return FULL_NAME; }
        public String getEmail() { return EMAIL; }
    }
}