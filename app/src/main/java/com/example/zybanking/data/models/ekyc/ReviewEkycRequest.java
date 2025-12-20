package com.example.zybanking.data.models.ekyc;

import com.google.gson.annotations.SerializedName;

public class ReviewEkycRequest {
    @SerializedName("status")
    private String status; // "APPROVED" hoặc "REJECTED"

    @SerializedName("note")
    private String note; // Ghi chú lý do nếu từ chối

    public ReviewEkycRequest() {}

    // Getter & Setter...
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}