package com.example.zybanking.data.models.transaction;

import com.google.gson.annotations.SerializedName;

public class Transaction {
    @SerializedName("TRANSACTION_ID")
    private String transactionId;

    @SerializedName("AMOUNT")
    private double amount;

    @SerializedName("STATUS")
    private String status;

    @SerializedName("CREATED_AT")
    private String createdAt;

    @SerializedName("TYPE")
    private String type; // 'transfer', 'utility', 'deposit'

    @SerializedName("DEST_ACC_NAME")
    private String destName;

    // --- BỔ SUNG THÊM 2 TRƯỜNG NÀY ---
    @SerializedName("DEST_BANK_CODE")
    private String destBankCode; // Lưu nhà cung cấp: EVN, CAPNUOC, TOPUP...

    @SerializedName("DEST_ACC_NUM")
    private String destAccNum; // Lưu mã khách hàng hoặc số điện thoại
    // --------------------------------

    // Getters
    public String getTransactionId() { return transactionId; }
    public double getAmount() { return amount; }
    public String getStatus() { return status; }
    public String getCreatedAt() { return createdAt; }
    public String getDestName() { return destName; }
    public String getType() { return type; }

    // Getters mới bổ sung
    public String getDestBankCode() { return destBankCode; }
    public String getDestAccNum() { return destAccNum; }
}