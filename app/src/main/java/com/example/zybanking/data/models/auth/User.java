package com.example.zybanking.data.models.auth;

import com.google.gson.annotations.SerializedName;

public class User {
    // Ánh xạ tên cột trong JSON (viết hoa từ Database) sang biến Java
    @SerializedName("USER_ID")
    private String userId;

    @SerializedName("FULL_NAME")
    private String fullName;

    @SerializedName("EMAIL")
    private String email;

    @SerializedName("PHONE")
    private String phone;

    @SerializedName("ROLE")
    private String role;

    @SerializedName("IS_ACTIVE")
    private int isActive; // Database lưu bool/tinyint thường trả về 0 hoặc 1

    // Constructor, Getters
    public String getUserId() { return userId; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getRole() { return role; }
    public boolean isActive() { return isActive == 1; }
}