package com.example.zybanking.data.remote;

import com.example.zybanking.data.models.AccountSummaryResponse;
import com.example.zybanking.data.models.BasicResponse;
import com.example.zybanking.data.models.auth.UserListResponse;
import com.example.zybanking.data.models.ekyc.EkycListResponse;
import com.example.zybanking.data.models.ekyc.EkycRequest;
import com.example.zybanking.data.models.ekyc.EkycResponse;
import com.example.zybanking.data.models.transaction.BillListResponse;
import com.example.zybanking.data.models.transaction.BillPayRequest;
import com.example.zybanking.data.models.transaction.BillResponse;
import com.example.zybanking.data.models.Branch;
import com.example.zybanking.data.models.auth.ChangePasswordRequest;
import com.example.zybanking.data.models.transaction.DepositRequest;
import com.example.zybanking.data.models.auth.ForgotPasswordRequest;
import com.example.zybanking.data.models.auth.ForgotPasswordResponse;
import com.example.zybanking.data.models.auth.LoginRequest;
import com.example.zybanking.data.models.auth.LoginResponse;
import com.example.zybanking.data.models.transaction.MortgagePaymentRequest;
import com.example.zybanking.data.models.Notification;
import com.example.zybanking.data.models.OtpConfirmRequest;
import com.example.zybanking.data.models.auth.ResetPasswordRequest;
import com.example.zybanking.data.models.transaction.Transaction;
import com.example.zybanking.data.models.transaction.TransactionListResponse;
import com.example.zybanking.data.models.transaction.TransferRequest;
import com.example.zybanking.data.models.auth.UserResponse;
import com.example.zybanking.data.models.utils.UtilityResponse;
import com.example.zybanking.data.models.utils.UtilityTopupRequest;
import com.example.zybanking.data.models.auth.VerifyOtpRequest;
import com.example.zybanking.data.models.transaction.WithdrawRequest;

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
    @PUT("users/{user_id}")
    Call<BasicResponse> updateUser(
            @Path("user_id") String userId,
            @Body Map<String, Object> updateData
    );
    @GET("accounts/{account_id}/summary")
    Call<AccountSummaryResponse> getAccountSummary(@Path("account_id") String accountId);
    @POST("auth/change-password")
    Call<BasicResponse> changePassword(
            @Header("Authorization") String token,
            @Body ChangePasswordRequest request);
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
    @POST("transactions/mortgage/pay")
    Call<BasicResponse> payMortgage(@Body MortgagePaymentRequest body);

//LOCATON
    @GET("branches/nearby")
    Call<List<Branch>> getNearbyBranches(
            @Query("lat") double lat,
            @Query("lng") double lng,
            @Query("radius_m") int radius
    );

    @GET("branches/{branch_id}/route")
    Call<ResponseBody> getBranchRoute(
            @Path("branch_id") String branchId,
            @Query("from_lat") double fromLat,
            @Query("from_lng") double fromLng
    );

    //NOTI
    @GET("notifications/{userId}")
    Call<List<Notification>> getNotifications(@Path("userId") String userId);

    //ekyc
        @POST("ekyc/create")
        Call<BasicResponse> submitEKYC(
                @Header("Authorization") String token,
                @Body EkycRequest request
        );

    @POST("users/{user_id}/ekyc")
    Call<BasicResponse> createEkyc(
            @Header("Authorization") String token,
            @Path("user_id") String userId,
            @Body EkycRequest request
    );
    // Lấy thông tin eKYC (Khớp với get_ekyc_route)
    @GET("users/{user_id}/ekyc")
    Call<EkycResponse> getMyEkyc(@Path("user_id") String userId);

    // --- Dành cho ADMIN ---

    // Lấy danh sách chờ (Khớp với get_pending)
    @GET("users/pending")
    Call<EkycListResponse> getPendingEkyc(@Header("Authorization") String token);

    @PUT("users/{user_id}/ekyc/review")
    Call<BasicResponse> reviewEkyc(
            @Header("Authorization") String token,
            @Path("user_id") String userId,
            @Body Map<String, Object> reviewData
    );
    @PATCH("users/{user_id}/ekyc")
    Call<BasicResponse> updateEkyc(
            @Header("Authorization") String token,
            @Path("user_id") String userId,
            @Body EkycRequest request
    );
    @GET("accounts/rates")
    Call<Map<String, Object>> getInterestRates(@Header("Authorization") String token);

    @PUT("accounts/rates")
    Call<BasicResponse> updateInterestRates(@Header("Authorization") String token, @Body Map<String, Double> rates);
    @PUT("notifications/{userId}/read")
    Call<BasicResponse> markNotificationsAsRead(@Path("userId") String userId);
    // data/remote/ApiService.java

    @PUT("notifications/mark-read/{notiId}")
    Call<BasicResponse> markSingleNotificationAsRead(@Path("notiId") String notiId);

    @GET("admin/dashboard/stats")
    Call<Map<String, Object>> getAdminStats(
            @Header("Authorization") String token
    );

    @GET("admin/dashboard/weekly-transactions")
    Call<List<Map<String, Object>>> getWeeklyTransactions(
            @Header("Authorization") String token
    );
    @GET("admin/users")
    Call<UserListResponse> getAdminUsers(@Header("Authorization") String token, @Query("search") String search);

    @GET("admin/transactions")
    Call<TransactionListResponse> getAdminTransactions(
            @Header("Authorization") String token,
            @Query("search") String search,
            @Query("status") String status
    );
}

