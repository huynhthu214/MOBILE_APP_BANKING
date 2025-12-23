package com.example.zybanking.data.models.account;

import com.example.zybanking.data.models.transaction.Transaction;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class AccountSummaryResponse {
    @SerializedName("status")
    public String status;

    @SerializedName("data")
    public AccountData data;

    // --- Inner Class chứa dữ liệu thực tế ---
    public static class AccountData {
        @SerializedName("ACCOUNT_ID")
        public String accountId;

        @SerializedName("ACCOUNT_NUMBER")
        public String accountNumber;

        @SerializedName("BALANCE")
        public Double balance;

        @SerializedName("ACCOUNT_TYPE")
        public String type; // CHECKING, SAVING, MORTGAGE

        @SerializedName("INTEREST_RATE")
        public Double interestRate;
        @SerializedName("TERM_MONTHS")
        public Integer termMonths;
        @SerializedName("FULL_NAME")
        public String ownerName;

        @SerializedName("STATUS")
        public String accountStatus;

        @SerializedName("last_transactions")
        public List<Transaction> lastTransactions;

        // --- CÁC TRƯỜNG CHO MORTGAGE (KHOẢN VAY) ---

        @SerializedName(value = "TOTAL_LOAN_AMOUNT", alternate = {"total_loan_amount"})
        public Double totalLoanAmount;

        // 2. Sửa khớp với lỗi chính tả PAYMEN_FREQUENCY trong DB
        @SerializedName(value = "PAYMEN_FREQUENCY", alternate = {"payment_frequency", "PAYMENT_FREQUENCY"})
        public String paymentFrequency;

        // Các trường này có vẻ đã đúng nếu backend trả về viết hoa
        @SerializedName("PAYMENT_AMOUNT")
        public Double paymentAmount;

        @SerializedName("NEXT_PAYMENT_DATE")
        public String nextPaymentDate;

        @SerializedName("REMAINING_BALANCE")
        public Double remainingBalance;
        // --- CÁC TRƯỜNG CHO SAVING ---
        @SerializedName("PRINCIPAL_AMOUNT")
        public Double principalAmount;

        @SerializedName("maturity_date")
        public String maturityDate;
        @SerializedName("monthly_interest")
        public Double monthlyInterest;
    }


    public String getType() { return data != null ? data.type : ""; }
    public Double getBalance() { return data != null ? data.balance : 0.0; }
    public String getAccountNumber() { return data != null ? data.accountNumber : ""; }
    public String getOwnerName() { return data != null ? data.ownerName : ""; }
    public String getStatus() { return data != null ? data.accountStatus : ""; }
    public Double getPrincipal() { return data != null ? data.principalAmount : 0.0; }
    public Double getPaymentAmount() { return data != null ? data.paymentAmount : 0.0; }
    public Double getInterestRate() { return data != null ? data.interestRate : 0.0; }
}