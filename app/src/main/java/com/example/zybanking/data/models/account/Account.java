package com.example.zybanking.data.models.account;

import com.google.gson.annotations.SerializedName;

public class Account {
    // Ánh xạ chính xác với cột trong bảng ACCOUNT (ảnh image_4cb881.png)
    @SerializedName("ACCOUNT_ID") private String accountId;
    @SerializedName("ACCOUNT_NUMBER") private String accountNumber;
    @SerializedName("ACCOUNT_TYPE") private String accountType;
    @SerializedName("BALANCE") private Double balance;
    @SerializedName("STATUS") private String status;
    @SerializedName("CREATED_AT") private String createdAt;
    @SerializedName("USER_ID") private String userId;

    // Trường này do câu lệnh JOIN bên backend trả về
    @SerializedName("FULL_NAME") private String ownerName;

    // Helper: Trả về tên chủ thẻ (nếu null thì trả về N/A)
    public String getOwnerName() { return ownerName != null ? ownerName : "N/A"; }
    public void setOwnerName(String name) { this.ownerName = name; }

    public String getAccountId() { return accountId; }
    public String getAccountNumber() { return accountNumber; }
    public String getAccountType() { return accountType != null ? accountType : ""; }
    public Double getBalance() { return balance != null ? balance : 0.0; }
    public String getStatus() { return status; }
    public void setBalance(Double balance) { this.balance = balance; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    public void setAccountType(String accountType) { this.accountType = accountType; }
}