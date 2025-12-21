package com.example.zybanking.data.models.ekyc;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class EkycListResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private List<EkycItem> data;

    public List<EkycItem> getData() {
        return data;
    }

    public static class EkycItem {
        // Ánh xạ với cột USER_ID trong Database
        @SerializedName("USER_ID")
        public String userId;

        // Ánh xạ với cột FULL_NAME trong bảng USER
        @SerializedName("FULL_NAME")
        public String fullName;

        // Ánh xạ với cột EMAIL trong bảng USER
        @SerializedName("EMAIL")
        public String email;

        // Ánh xạ với cột IMG_FRONT_URL trong bảng EKYC
        @SerializedName("IMG_FRONT_URL")
        public String imgFront;

        // Ánh xạ với cột IMG_BACK_URL trong bảng EKYC
        @SerializedName("IMG_BACK_URL")
        public String imgBack;

        // Ánh xạ với cột SELFIE_URL trong bảng EKYC
        @SerializedName("SELFIE_URL")
        public String selfie;

        @SerializedName("STATUS")
        public String status;
    }
}