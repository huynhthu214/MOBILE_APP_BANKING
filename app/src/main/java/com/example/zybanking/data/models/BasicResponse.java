package com.example.zybanking.data.models;

public class BasicResponse {
    public String status;
    public String message;
    public String transaction_id;
    public String utility_payment_id;
    public String full_name;

    // Add this helper method
    public boolean isSuccess() {
        // Checks if status is not null and equals "success" (case-insensitive)
        return status != null && "success".equalsIgnoreCase(status);
    }
}