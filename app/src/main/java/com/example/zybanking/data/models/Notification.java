package com.example.zybanking.data.models;

public class Notification {
    private int id;
    private String title;
    private String description;
    private String time;
    private int type; // 1: Giao dịch (Tiền), 2: Hệ thống (Cảnh báo), 3: Người dùng (eKYC/TK)
    private boolean isRead;

    public Notification(int id, String title, String description, String time, int type, boolean isRead) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.time = time;
        this.type = type;
        this.isRead = isRead;
    }

    // Getters
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getTime() { return time; }
    public int getType() { return type; }
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
}