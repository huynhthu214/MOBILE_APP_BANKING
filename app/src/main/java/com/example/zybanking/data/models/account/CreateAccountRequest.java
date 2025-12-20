package com.example.zybanking.data.models.account;

import com.google.gson.annotations.SerializedName;

public class CreateAccountRequest {
    @SerializedName("user_id")
    private String userId;

    @SerializedName("account_type")
    private String accountType; // "CHECKING", "SAVING", "MORTGAGE"

    @SerializedName("balance")
    private Double balance;

    @SerializedName("currency")
    private String currency; // "VND"

    public CreateAccountRequest() {}

    // Getter & Setter...
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }
    public Double getBalance() { return balance; }
    public void setBalance(Double balance) { this.balance = balance; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
}