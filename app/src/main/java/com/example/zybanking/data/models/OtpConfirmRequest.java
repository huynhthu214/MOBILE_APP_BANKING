package com.example.zybanking.data.models;
import com.google.gson.annotations.SerializedName;

public class OtpConfirmRequest {
    @SerializedName("transaction_id")
    private String transactionId;

    @SerializedName("otp_code")
    private String otpCode;

    // Constructor 2 tham số
    public OtpConfirmRequest(String transactionId, String otpCode) {
        this.transactionId = transactionId;
        this.otpCode = otpCode;
    }
}
