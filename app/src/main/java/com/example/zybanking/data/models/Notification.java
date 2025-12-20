package com.example.zybanking.data.models;

import com.google.gson.annotations.SerializedName;

public class Notification {
    @SerializedName("NOTI_ID")
    public String notiId;

    @SerializedName("USER_ID")
    public String userId;

    @SerializedName("TITLE")
    public String title;

    @SerializedName("BODY")
    public String body;

    @SerializedName("TYPE")
    public String type; // TRANSACTION, SECURITY, PROMOTION...

    @SerializedName("IS_READ")
    public int isRead; // 0 hoặc 1

    @SerializedName("CREATED_AT")
    public String createdAt;
    public String getTitle() { return title; }
    public String getBody() { return body; }
    public String getCreatedAt() { return createdAt; }
    public int getIsRead() { return isRead; }
    public String getType() { return type; }
}