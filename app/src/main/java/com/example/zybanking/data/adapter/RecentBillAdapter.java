package com.example.zybanking.data.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.zybanking.R;
import com.example.zybanking.data.models.transaction.Transaction; // Đảm bảo import đúng model của bạn

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class RecentBillAdapter extends RecyclerView.Adapter<RecentBillAdapter.ViewHolder> {

    private List<Transaction> transactionList;

    public RecentBillAdapter(List<Transaction> transactionList) {
        this.transactionList = transactionList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recent_bill, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaction tx = transactionList.get(position);

        // Format tiền tệ
        double amount = tx.getAmount(); // Giả sử model có getAmount()
        String formattedAmount = NumberFormat.getCurrencyInstance(new Locale("vi", "VN")).format(amount);
        holder.tvAmount.setText("-" + formattedAmount);

        // Hiển thị ngày (Cần format lại chuỗi datetime nếu cần)
        holder.tvDate.setText(tx.getCreatedAt());

        // Xử lý Icon và Tên dựa theo loại giao dịch hoặc provider
        String provider = tx.getDestBankCode(); // Lấy mã nhà cung cấp (EVN, CAPNUOC, vv)

        if (provider != null && provider.contains("EVN")) {
            holder.tvTitle.setText("Điện lực: " + tx.getDestAccNum());
            holder.imgIcon.setImageResource(android.R.drawable.ic_lock_idle_charging);
            holder.imgIcon.setBackgroundColor(Color.parseColor("#FFF3CD")); // Màu vàng nhạt
            holder.imgIcon.setColorFilter(Color.parseColor("#F4B400"));    // Màu vàng đậm
        } else if (provider != null && provider.contains("NUOC")) {
            holder.tvTitle.setText("Tiền Nước: " + tx.getDestAccNum());
            holder.imgIcon.setImageResource(android.R.drawable.ic_menu_compass);
            holder.imgIcon.setBackgroundColor(Color.parseColor("#E3F2FD")); // Màu xanh nhạt
            holder.imgIcon.setColorFilter(Color.parseColor("#1E88E5"));    // Màu xanh đậm
        } else if ("TOPUP_SERVICE".equals(provider)) {
            holder.tvTitle.setText("Nạp điện thoại: " + tx.getDestAccNum());
            holder.imgIcon.setImageResource(android.R.drawable.ic_menu_call);
            holder.imgIcon.setBackgroundColor(Color.parseColor("#DCFCE7"));
            holder.imgIcon.setColorFilter(Color.parseColor("#16A34A"));
        } else {
            holder.tvTitle.setText("Thanh toán hóa đơn");
        }

        // Trạng thái
        if ("COMPLETED".equalsIgnoreCase(tx.getStatus())) {
            holder.tvStatus.setText("Thành công");
            holder.tvStatus.setTextColor(Color.parseColor("#16A34A"));
            holder.tvStatus.setBackgroundColor(Color.parseColor("#DCFCE7"));
        } else {
            holder.tvStatus.setText("Chờ xử lý");
            holder.tvStatus.setTextColor(Color.parseColor("#F4B400"));
            holder.tvStatus.setBackgroundColor(Color.parseColor("#FFF3CD"));
        }
    }

    @Override
    public int getItemCount() {
        return transactionList == null ? 0 : transactionList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDate, tvAmount, tvStatus;
        ImageView imgIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_bill_title);
            tvDate = itemView.findViewById(R.id.tv_bill_date);
            tvAmount = itemView.findViewById(R.id.tv_bill_amount);
            tvStatus = itemView.findViewById(R.id.tv_bill_status);
            imgIcon = itemView.findViewById(R.id.img_service_icon);
        }
    }
}