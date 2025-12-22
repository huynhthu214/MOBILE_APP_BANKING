package com.example.zybanking.data.models.account;

import com.example.zybanking.data.models.transaction.Transaction;
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
    @SerializedName("status_acc") public String statusAcc;
    @SerializedName("full_name")
    private String ownerName;
    public String getStatusAcc() { return statusAcc; }

    public String getType() {
        return type;
    }

    public double getBalance() {
        return balance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public double getPrincipal() {
        return principalAmount;
    }

    public double getPaymentAmount() {
        return paymentAmount;
    }

    public String getOwnerName() {
        return ownerName;
    }
    public String getStatus() { return status; }
    public double getRemainingBalance() {
        return remainingBalance;
    }
}