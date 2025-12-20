package com.example.zybanking.data.models.ekyc;

import com.google.gson.annotations.SerializedName;

public class EkycResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private EkycData data;

    public String getStatus() { return status; }
    public EkycData getData() { return data; }

    public static class EkycData {
        @SerializedName("STATUS")
        public String status; // "pending", "approved", "rejected"

        @SerializedName("EKYC_ID")
        public String ekycId;
    }
}