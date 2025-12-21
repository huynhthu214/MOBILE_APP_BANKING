package com.example.zybanking.data.models.ekyc;

import com.google.gson.annotations.SerializedName;

public class EkycResponse {
    @SerializedName("status")
    public String status;

    @SerializedName("data")
    public EkycData data;

    public static class EkycData {
        // Tên biến trong @SerializedName PHẢI KHỚP 100% với tên cột trong Database/Python trả về
        @SerializedName("STATUS")
        public String status;

        @SerializedName("IMG_FRONT_URL")
        public String frontUrl;

        @SerializedName("IMG_BACK_URL")
        public String backUrl;

        @SerializedName("SELFIE_URL")
        public String selfieUrl;

        // Thêm các trường khác nếu cần (ví dụ REVIEWED_AT)
    }
}