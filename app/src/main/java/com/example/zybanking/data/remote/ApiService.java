package com.example.zybanking.data.remote;

import com.example.zybanking.data.models.AccountSummaryResponse;
import com.example.zybanking.data.models.ForgotPasswordRequest;
import com.example.zybanking.data.models.ForgotPasswordResponse;
import com.example.zybanking.data.models.LoginRequest;
import com.example.zybanking.data.models.LoginResponse;
import com.example.zybanking.data.models.ResetPasswordRequest;
import com.example.zybanking.data.models.Transaction;
import com.example.zybanking.data.models.UserResponse;
import com.example.zybanking.data.models.VerifyOtpRequest;

import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.http.*;
public interface ApiService {
    // Login
    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);
    // Forgot password - gửi OTP
    @POST("auth/forgot-password")
    Call<ForgotPasswordResponse> forgotPassword(
            @Body ForgotPasswordRequest request
    );
    // Verify OTP
    @POST("otp/verify")
    Call<ForgotPasswordResponse> verifyOtp(
            @Body VerifyOtpRequest request
    );
    // Reset password
    @POST("auth/reset-password")
    Call<ForgotPasswordResponse> resetPassword(
            @Body ResetPasswordRequest request
    );
    // Resend OTP
    @POST("otp/resend")
    Call<ForgotPasswordResponse> resendOtp(@Body Map<String, String> body);

    // Logout
    @POST("auth/logout")
    Call<Map<String, Object>> logout(
            @Header("Authorization") String authHeader,
            @Body Map<String, String> body
    );

    @GET("users/me")
    Call<UserResponse> getCurrentUser(@Header("Authorization") String token);

    @GET("accounts/{account_id}/summary")
    Call<AccountSummaryResponse> getAccountSummary(@Path("account_id") String accountId);

    @GET("transactions/recent")
    Call<List<Transaction>> getRecentTransactions(
            @Query("user_id") String userId,
            @Query("limit") int limit
    );

}

