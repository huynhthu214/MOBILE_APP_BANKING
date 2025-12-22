package com.example.zybanking.data.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zybanking.R;
import com.example.zybanking.data.models.transaction.Transaction;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    private Context context;
    private List<Transaction> list;

    public TransactionAdapter(Context context, List<Transaction> list) {
        this.context = context;
        this.list = list;
    }

    // Hàm update dữ liệu mới mà không cần tạo lại Adapter
    public void updateData(List<Transaction> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Đảm bảo tên layout đúng với file xml của bạn (VD: item_transaction.xml)
        View view = LayoutInflater.from(context)
                .inflate(R.layout.basic_list_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaction item = list.get(position);
        if (item == null) return;

        // --- BƯỚC 1: XÁC ĐỊNH LOẠI GIAO DỊCH & MÀU SẮC ---
        boolean isIncome = checkIsIncome(item.getType());

        if (isIncome) {
            // Nạp tiền: Màu Xanh lá, Dấu +
            holder.tvAmount.setTextColor(Color.parseColor("#4CAF50"));
            holder.tvAmount.setText("+ " + formatCurrency(item.getAmount()));
        } else {
            // Rút/Chuyển: Màu Đỏ, Dấu -
            holder.tvAmount.setTextColor(Color.parseColor("#F44336"));
            holder.tvAmount.setText("- " + formatCurrency(item.getAmount()));
        }

        // --- BƯỚC 2: HIỂN THỊ TIÊU ĐỀ ---
        String type = item.getType() != null ? item.getType().toUpperCase() : "UNKNOWN";

        if ("DEPOSIT".equals(type)) {
            holder.tvTitle.setText("Nạp tiền vào tài khoản");
        } else if ("WITHDRAW".equals(type)) {
            holder.tvTitle.setText("Rút tiền mặt");
        } else if ("TRANSFER".equals(type)) {
            // Nếu có tên người nhận thì hiện tên, không thì hiện chung chung
            if (item.getDestName() != null && !item.getDestName().isEmpty()) {
                holder.tvTitle.setText("Chuyển đến: " + item.getDestName());
            } else {
                holder.tvTitle.setText("Chuyển khoản");
            }
        } else {
            holder.tvTitle.setText("Giao dịch khác");
        }

        // --- BƯỚC 3: HIỂN THỊ THỜI GIAN ---
        holder.tvTime.setText(formatDate(item.getCreatedAt()));
    }

    @Override
    public int getItemCount() {
        return (list != null) ? list.size() : 0;
    }

    // ================= HELPER FUNCTIONS =================

    // Kiểm tra xem là Thu (+) hay Chi (-)
    private boolean checkIsIncome(String type) {
        if (type == null) return false;
        // Logic: Chỉ có DEPOSIT (nạp) là tiền vào, còn lại là ra
        return type.toUpperCase().contains("DEPOSIT");
    }

    // Format tiền tệ (VD: 500,000 đ)
    private String formatCurrency(double amount) {
        try {
            DecimalFormat formatter = new DecimalFormat("#,###");
            return formatter.format(amount) + " đ";
        } catch (Exception e) {
            return String.valueOf(amount);
        }
    }

    // Format ngày giờ (ISO 8601 -> HH:mm dd/MM/yyyy)
    private String formatDate(String isoDate) {
        if (isoDate == null) return "";
        try {
            // Input: 2023-12-22T14:30:00
            SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            Date date = input.parse(isoDate);

            // Output: 14:30 22/12/2023
            SimpleDateFormat output = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault());
            return output.format(date);
        } catch (Exception e) {
            return isoDate; // Lỗi thì trả về nguyên gốc
        }
    }

    // ================= VIEW HOLDER =================
    // ViewHolder chỉ làm nhiệm vụ ánh xạ View, KHÔNG chứa logic if-else
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvTime, tvAmount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvTransactionTitle);
            tvTime = itemView.findViewById(R.id.tvTransactionDate);
            tvAmount = itemView.findViewById(R.id.tvTransactionAmount);
        }
    }
}