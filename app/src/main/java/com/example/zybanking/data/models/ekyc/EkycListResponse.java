package com.example.zybanking.data.models.ekyc;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EkycListResponse {
    @SerializedName("status")
    private String status;
    @SerializedName("data")
    private List<EkycItem> data;

    public List<EkycItem> getData() { return data; }

    public static class EkycItem {
        @SerializedName("USER_ID") public String userId;
        @SerializedName("EKYC_ID") public String ekycId;
        @SerializedName("IMG_FRONT_URL") public String imgFront;
        @SerializedName("IMG_BACK_URL") public String imgBack;
        @SerializedName("SELFIE_URL") public String selfie;
        @SerializedName("CREATED_AT") public String createdAt;
        // Nếu có JOIN với bảng USER như hàm get_ekyc_by_id
        @SerializedName("FULL_NAME") public String fullName;
    }
}