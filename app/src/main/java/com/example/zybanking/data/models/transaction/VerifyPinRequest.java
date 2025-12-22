package com.example.zybanking.data.models.transaction;

public class VerifyPinRequest {
    public String transaction_id;
    public String pin_code;

    public VerifyPinRequest(String transaction_id, String pin_code) {
        this.transaction_id = transaction_id;
        this.pin_code = pin_code;
    }
}
