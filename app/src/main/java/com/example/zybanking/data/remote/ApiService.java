package com.example.zybanking.data.remote;

import com.example.zybanking.data.models.AccountSummaryResponse;
import com.example.zybanking.data.models.BasicResponse;
import com.example.zybanking.data.models.BillListResponse;
import com.example.zybanking.data.models.BillPayRequest;
import com.example.zybanking.data.models.BillResponse;
import com.example.zybanking.data.models.DepositRequest;
import com.example.zybanking.data.models.ForgotPasswordRequest;
import com.example.zybanking.data.models.ForgotPasswordResponse;
import com.example.zybanking.data.models.LoginRequest;
import com.example.zybanking.data.models.LoginResponse;
import com.example.zybanking.data.models.OtpConfirmRequest;
import com.example.zybanking.data.models.ResetPasswordRequest;
import com.example.zybanking.data.models.Transaction;
import com.example.zybanking.data.models.TransactionHistoryResponse;
import com.example.zybanking.data.models.TransferRequest;
import com.example.zybanking.data.models.UserResponse;
import com.example.zybanking.data.models.UtilityResponse;
import com.example.zybanking.data.models.UtilityTopupRequest;
import com.example.zybanking.data.models.VerifyOtpRequest;
import com.example.zybanking.data.models.WithdrawRequest;

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

    @POST("transactions/transfer/create")
    Call<BasicResponse> transferCreate(@Body TransferRequest body);

    @POST("transactions/transfer/confirm")
    Call<BasicResponse> transferConfirm(@Body OtpConfirmRequest body);

    @POST("transactions/deposit/create")
    Call<BasicResponse> deposit(
            @Header("Authorization") String token,
            @Body DepositRequest request
    );

    @POST("transactions/deposit/confirm")
    Call<BasicResponse> depositConfirm(@Body OtpConfirmRequest body);

    @GET("transactions/history")
    Call<TransactionHistoryResponse> getTransactionHistory(
            @Header("Authorization") String token
    );

    @POST("transactions/withdraw/create")
    Call<BasicResponse> withdraw(@Body WithdrawRequest body);

    @POST("transactions/withdraw/confirm")
    Call<BasicResponse> withdrawConfirm(@Body OtpConfirmRequest body);

    // BILL
    @GET("bills")
    Call<BillListResponse> listBills(@Query("keyword") String keyword);

    @GET("bills/{bill_id}")
    Call<BillResponse> getBill(@Path("bill_id") String billId);

    @POST("bills/pay")
    Call<BasicResponse> payBill(@Body BillPayRequest body);

    // UTILITY
    @POST("utility/topup")
    Call<BasicResponse> utilityTopup(@Body UtilityTopupRequest body);
    @POST("utility/confirm")
    Call<BasicResponse> utilityConfirm(@Body OtpConfirmRequest body);
    @GET("utility/{utility_payment_id}")
    Call<UtilityResponse> getUtilityDetail(@Path("utility_payment_id") String id);

}

