package com.example.zybanking.data.remote;

import com.example.zybanking.data.models.LoginRequest;
import com.example.zybanking.data.models.LoginResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {

    // Login
    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);
}
