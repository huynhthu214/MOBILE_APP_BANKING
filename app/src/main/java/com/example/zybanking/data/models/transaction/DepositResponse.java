package com.example.zybanking.data.models.transaction;

import com.google.gson.annotations.SerializedName;

public class DepositResponse {

    @SerializedName("status")
    public String status;

    @SerializedName("message")
    public String message;

    @SerializedName("transaction_id")
    public String transactionId;
}
