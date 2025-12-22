package com.example.zybanking.data.models.account;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class AccountListResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private List<Account> data; // Đây là danh sách tài khoản thực sự

    public List<Account> getData() {
        return data;
    }

    public void setData(List<Account> data) {
        this.data = data;
    }
}