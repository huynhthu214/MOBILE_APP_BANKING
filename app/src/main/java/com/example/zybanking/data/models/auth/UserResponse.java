package com.example.zybanking.data.models.auth;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Map;

// IMPORT Class User độc lập
import com.example.zybanking.data.models.auth.User;

public class UserResponse {
    @SerializedName("status") public String status;
    @SerializedName("data") public Data data;

    public Data getData() { return data; }

    public static class Data {
        @SerializedName("user")
        private User user; // Đây là class User độc lập (đã import ở trên)

        @SerializedName("ekyc")
        private Ekyc ekyc; // Class Ekyc nằm bên dưới

        @SerializedName("accounts")
        private List<Map<String, Object>> accounts;

        public User getUser() { return user; }
        public Ekyc getEkyc() { return ekyc; }
        public List<Map<String, Object>> getAccounts() { return accounts; }
    }

    // Class Ekyc giữ nguyên ở đây
    public static class Ekyc {
        @SerializedName("EKYC_ID") private String ekycId;
        @SerializedName("STATUS") private String status;
        public String getStatus() { return status != null ? status : "Chưa xác thực"; }
    }
}