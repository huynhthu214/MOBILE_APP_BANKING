package com.example.zybanking.data.models.auth;

import com.google.gson.annotations.SerializedName;

public class User {
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

    @SerializedName("CREATED_AT")
    private String createdAt;

    @SerializedName("IS_ACTIVE")
    private int isActive;

    // Getters
    public String getUserId() { return userId; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getRole() { return role; }
    public String getCreatedAt() { return createdAt; }
    public boolean isActive() { return isActive == 1; }
}