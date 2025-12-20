package com.example.zybanking.data.models.ekyc;

import com.google.gson.annotations.SerializedName;

public class EkycRequest {
    @SerializedName("ID_IMG_FRONT_URL") // Khớp với data.get("ID_IMG_FRONT_URL") trong Python
    private String imgFrontUrl;

    @SerializedName("ID_IMG_BACK_URL") // Khớp với data.get("ID_IMG_BACK_URL") trong Python
    private String imgBackUrl;

    @SerializedName("SELFIE_URL")      // Khớp với data.get("SELFIE_URL") trong Python
    private String selfieUrl;

    public EkycRequest() {}

    public String getImgFrontUrl() { return imgFrontUrl; }
    public void setImgFrontUrl(String imgFrontUrl) { this.imgFrontUrl = imgFrontUrl; }

    public String getImgBackUrl() { return imgBackUrl; }
    public void setImgBackUrl(String imgBackUrl) { this.imgBackUrl = imgBackUrl; }

    public String getSelfieUrl() { return selfieUrl; }
    public void setSelfieUrl(String selfieUrl) { this.selfieUrl = selfieUrl; }
}