package com.example.zybanking.data.models;

public class OTP {
    private String code;
    private long expiryTime;
    private String userId;

    public OTP() {}

    public OTP(String code, long expiryTime, String userId) {
        this.code = code;
        this.expiryTime = expiryTime;
        this.userId = userId;
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public long getExpiryTime() { return expiryTime; }
    public void setExpiryTime(long expiryTime) { this.expiryTime = expiryTime; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
}
