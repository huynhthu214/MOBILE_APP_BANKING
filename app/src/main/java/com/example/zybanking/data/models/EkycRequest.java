package com.example.zybanking.data.models;

import com.google.gson.annotations.SerializedName;

public class EkycRequest {
    @SerializedName("img_front_url")
    private String imgFrontUrl;

    @SerializedName("img_back_url")
    private String imgBackUrl;

    @SerializedName("selfie_url")
    private String selfieUrl;

    public EkycRequest() {}

    // Getter & Setter...
    public String getImgFrontUrl() { return imgFrontUrl; }
    public void setImgFrontUrl(String imgFrontUrl) { this.imgFrontUrl = imgFrontUrl; }
    public String getImgBackUrl() { return imgBackUrl; }
    public void setImgBackUrl(String imgBackUrl) { this.imgBackUrl = imgBackUrl; }
    public String getSelfieUrl() { return selfieUrl; }
    public void setSelfieUrl(String selfieUrl) { this.selfieUrl = selfieUrl; }
}