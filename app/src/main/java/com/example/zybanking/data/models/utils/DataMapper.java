package com.example.zybanking.data.models.utils;

import com.example.zybanking.data.models.TransactionHistoryItem;
import com.example.zybanking.data.models.transaction.Transaction;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DataMapper {

    // Hàm chuyển đổi 1 thằng DTO -> UI Model
    public static TransactionHistoryItem toHistoryItem(Transaction dto) {

        // 1. Xử lý Title và Income dựa trên Type
        String title = "";
        boolean isIncome = false;

        // Lưu ý: Đảm bảo string khớp với server trả về (thường server trả về chữ hoa hoặc thường)
        String type = dto.getType() != null ? dto.getType().toUpperCase() : "UNKNOWN";

        switch (type) {
            case "DEPOSIT":
                title = "Nạp tiền vào tài khoản";
                isIncome = true; // Tiền vào (+)
                break;
            case "WITHDRAW":
                title = "Rút tiền mặt";
                isIncome = false; // Tiền ra (-)
                break;
            case "TRANSFER":
                // Nếu có tên người nhận thì hiện, không thì hiện chung chung
                if (dto.getDestName() != null && !dto.getDestName().isEmpty()) {
                    title = "Chuyển tiền đến " + dto.getDestName();
                } else {
                    title = "Chuyển khoản";
                }
                isIncome = false; // Tiền ra (-)
                break;
            default:
                title = "Giao dịch khác";
                isIncome = false;
        }

        // 2. Xử lý ngày tháng (Từ ISO 8601 sang format đẹp)
        String formattedTime = formatDate(dto.getCreatedAt());

        return new TransactionHistoryItem(
                dto.getTransactionId(),
                title,
                formattedTime,
                dto.getAmount(),
                isIncome
        );
    }

    private static String formatDate(String isoDate) {
        if (isoDate == null) return "";
        try {
            // Server gửi: 2023-12-22T14:30:00
            SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            Date date = input.parse(isoDate);

            // App hiện: 14:30 - 22/12/2023
            SimpleDateFormat output = new SimpleDateFormat("HH:mm - dd/MM/yyyy", Locale.getDefault());
            return output.format(date);
        } catch (Exception e) {
            return isoDate; // Lỗi thì trả về nguyên gốc
        }
    }
}