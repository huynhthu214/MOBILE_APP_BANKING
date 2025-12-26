package com.example.zybanking.data.models.transaction;

public class Bill {
    public String BILL_ID;
    public String PROVIDER;
    public double AMOUNT_DUE; // Bây giờ đã là kiểu số
    public String STATUS;
    public String DUE_DATE;
    public String BILL_TYPE; // Lưu ý: Nếu cột trong DB là BILL_TYPE thì dùng tên này
}