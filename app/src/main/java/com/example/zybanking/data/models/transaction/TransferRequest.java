package com.example.zybanking.data.models.transaction;

import com.google.gson.annotations.SerializedName;

public class TransferRequest {
    @SerializedName("from_account_id")
    public String from_account_id;

    @SerializedName("to_account_number")
    public String to_account_number;

    @SerializedName("amount")
    public double amount; // Dùng double để xử lý số tiền lớn

    @SerializedName("to_bank_code")
    public String to_bank_code;

    @SerializedName("note")
    public String note;

    // Cập nhật Constructor để nhận 5 tham số
    public TransferRequest(String from, String to, double amount, String bankCode, String note) {
        this.from_account_id = from;
        this.to_account_number = to;
        this.amount = amount;
        this.to_bank_code = bankCode;
        this.note = note;
    }
}