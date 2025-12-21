package com.example.zybanking.data.models.auth;

import com.google.gson.annotations.SerializedName;

// data/models/auth/CreateUserRequest.java
public class CreateUserRequest {
    @SerializedName("full_name")
    private String fullName;
    @SerializedName("email")
    private String email;
    @SerializedName("phone")
    private String phone;
    @SerializedName("password")
    private String password;

    public CreateUserRequest(String fullName, String email, String phone, String password) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.password = password;
    }
}