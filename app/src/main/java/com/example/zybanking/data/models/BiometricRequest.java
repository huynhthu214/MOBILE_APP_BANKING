package com.example.zybanking.data.models;

import com.google.gson.annotations.SerializedName;

public class BiometricRequest {
    @SerializedName("image_base64")
    private String imageBase64;

    @SerializedName("device_id") // Tuỳ chọn, nếu cần định danh thiết bị
    private String deviceId;

    public BiometricRequest() {}

    public BiometricRequest(String imageBase64) {
        this.imageBase64 = imageBase64;
    }

    public String getImageBase64() { return imageBase64; }
    public void setImageBase64(String imageBase64) { this.imageBase64 = imageBase64; }
}