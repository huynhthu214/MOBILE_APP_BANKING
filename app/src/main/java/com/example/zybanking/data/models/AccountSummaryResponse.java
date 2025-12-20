package com.example.zybanking.data.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class AccountSummaryResponse {
    @SerializedName("status")
    public String status;
    @SerializedName("type")
    public String type; // "checking", "saving", "mortgage"
    // Dữ liệu chung
    @SerializedName("balance")
    public Double balance;
    // Dữ liệu Checking
    @SerializedName("last_transactions")
    public List<Transaction> lastTransactions;
    // Dữ liệu Saving
    @SerializedName("interest_rate")
    public Double interestRate;
    @SerializedName("monthly_interest")
    public Double monthlyInterest;
    // Dữ liệu Mortgage
    @SerializedName("remaining_balance")
    public Double remainingBalance;
    @SerializedName("next_payment_date")
    public String nextPaymentDate;
    @SerializedName("account_number")
    public String accountNumber;
    @SerializedName("payment_amount")
    public Double paymentAmount;
    @SerializedName("principal_amount")
    public Double principalAmount;
    @SerializedName("maturity_date")
    public String maturityDate; // Ngày đáo hạn (cho cả Saving và Mortgage)
    @SerializedName("payment_frequency")
    public String paymentFrequency;
    @SerializedName("total_loan_amount")
    public Double totalLoanAmount;

}