package com.example.zybanking.data.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat; // Dùng cái này để lấy màu an toàn hơn
import androidx.recyclerview.widget.RecyclerView;

import com.example.zybanking.R;
// Import đúng đường dẫn model bạn vừa gửi
import com.example.zybanking.data.models.transaction.Transaction;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    private Context context;
    private List<Transaction> list; // Đã đổi sang List<Transaction>

    // Constructor nhận Context và List<Transaction>
    public TransactionAdapter(Context context, List<Transaction> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_transaction_placeholder, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaction item = list.get(position);

        // 1. Set Tên giao dịch (Hiển thị người nhận hoặc Loại giao dịch)
        if (item.getDestName() != null && !item.getDestName().isEmpty()) {
            holder.tvTitle.setText(item.getDestName());
        } else {
            // Nếu không có tên người nhận, hiển thị loại giao dịch
            holder.tvTitle.setText(item.getType() != null ? item.getType() : "Giao dịch");
        }

        // 2. Set Thời gian
        holder.tvTime.setText(item.getCreatedAt());

        // 3. Xử lý logic Tiền và Màu sắc (Thu/Chi)
        boolean isIncome = checkIsIncome(item);
        String formattedMoney = formatCurrency(item.getAmount());

        if (isIncome) {
            // Tiền vào (Màu xanh)
            holder.tvAmount.setText("+ " + formattedMoney);
            holder.tvAmount.setTextColor(ContextCompat.getColor(context, R.color.green)); // Đảm bảo bạn có màu này trong colors.xml
            holder.icon.setImageResource(android.R.drawable.ic_input_add); // Hoặc R.drawable.ic_arrow_down
        } else {
            // Tiền ra (Màu đỏ)
            holder.tvAmount.setText("- " + formattedMoney);
            holder.tvAmount.setTextColor(ContextCompat.getColor(context, R.color.red));   // Đảm bảo bạn có màu này trong colors.xml
            holder.icon.setImageResource(android.R.drawable.ic_delete); // Hoặc R.drawable.ic_arrow_up
        }
    }

    @Override
    public int getItemCount() {
        return (list != null) ? list.size() : 0;
    }

    // --- LOGIC KIỂM TRA THU HAY CHI ---
    // Bạn cần kiểm tra xem backend trả về "TYPE" là từ khóa gì để sửa ở đây
    private boolean checkIsIncome(Transaction item) {
        if (item.getType() == null) return false;

        String type = item.getType().toUpperCase();
        // Ví dụ: Nếu type là DEPOSIT (nạp tiền) hoặc RECEIVE (nhận tiền) -> Là thu nhập
        return type.contains("DEPOSIT") || type.contains("RECEIVE") || type.contains("INCOMING");
    }

    private String formatCurrency(double amount) {
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return format.format(amount);
    }

    // ViewHolder Class
    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView tvTitle, tvTime, tvAmount;

        ViewHolder(View v) {
            super(v);
            icon = v.findViewById(R.id.img_icon);
            tvTitle = v.findViewById(R.id.tv_title);
            tvTime = v.findViewById(R.id.tv_time);
            tvAmount = v.findViewById(R.id.tv_amount);
        }
    }
}