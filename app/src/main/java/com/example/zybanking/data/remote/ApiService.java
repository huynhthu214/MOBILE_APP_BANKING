package com.example.zybanking.data.remote;

import com.example.zybanking.data.models.ForgotPasswordRequest;
import com.example.zybanking.data.models.ForgotPasswordResponse;
import com.example.zybanking.data.models.LoginRequest;
import com.example.zybanking.data.models.LoginResponse;
import com.example.zybanking.data.models.ResetPasswordRequest;

import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {

    // Login
    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("auth/forgot-password")
    Call<ForgotPasswordResponse> forgotPassword(@Body ForgotPasswordRequest request);

    @POST("auth/reset-password")
    Call<ForgotPasswordResponse> resetPassword(@Body ResetPasswordRequest request);
}
