package com.example.zybanking.data.remote;

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
import com.example.zybanking.data.models.TransferRequest;
import com.example.zybanking.data.models.UtilityResponse;
import com.example.zybanking.data.models.UtilityTopupRequest;
import com.example.zybanking.data.models.WithdrawRequest;

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

    @POST("transactions/transfer/create")
    Call<BasicResponse> transferCreate(@Body TransferRequest body);

    @POST("transactions/transfer/confirm")
    Call<BasicResponse> transferConfirm(@Body OtpConfirmRequest body);

    @POST("transactions/deposit/create")
    Call<BasicResponse> deposit(@Body DepositRequest body);

    @POST("transactions/deposit/confirm")
    Call<BasicResponse> depositConfirm(@Body OtpConfirmRequest body);

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
