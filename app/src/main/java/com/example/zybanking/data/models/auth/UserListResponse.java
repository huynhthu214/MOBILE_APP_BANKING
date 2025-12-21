package com.example.zybanking.data.models.auth;

import java.util.List;

public class UserListResponse {
    private String status;
    private List<User> data; // Biến này phải trùng tên với key "data" trong JSON từ Flask

    public String getStatus() {
        return status;
    }

    public List<User> getData() {
        return data;
    }
}