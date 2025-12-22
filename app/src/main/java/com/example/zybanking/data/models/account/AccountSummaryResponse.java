package com.example.zybanking.data.models.account;

import com.example.zybanking.data.models.transaction.Transaction;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class AccountSummaryResponse {
    @SerializedName("status")
    public String status;

    @SerializedName("data")
    public AccountData data;

    public static class AccountData { // Nên dùng static để tránh rò rỉ bộ nhớ
        @SerializedName("account_id")
        public String accountId;

        @SerializedName("account_number")
        public String accountNumber;

        @SerializedName("balance")
        public Double balance;

        @SerializedName("type")
        public String type;

        @SerializedName("currency")
        public String currency;

        @SerializedName("interest_rate")
        public Double interestRate;

        @SerializedName("last_transactions")
        public List<Transaction> lastTransactions;

        // --- CÁC TRƯỜNG CHO MORTGAGE (KHOẢN VAY) ---
        @SerializedName("payment_amount")
        public Double paymentAmount;

        @SerializedName("next_payment_date")
        public String nextPaymentDate;

        @SerializedName("remaining_balance")
        public Double remainingBalance;

        @SerializedName("total_loan_amount") // Cần thêm trường này
        public Double totalLoanAmount;

        @SerializedName("monthly_interest")
        public Double monthlyInterest;

        @SerializedName("payment_frequency") // Cần thêm trường này
        public String paymentFrequency;

        @SerializedName("principal_amount")
        public Double principalAmount;

        @SerializedName("maturity_date")
        public String maturityDate;
    }
}