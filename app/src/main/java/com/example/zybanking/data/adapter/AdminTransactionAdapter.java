package com.example.zybanking.data.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zybanking.R;
import com.example.zybanking.data.models.transaction.Transaction;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class AdminTransactionAdapter extends RecyclerView.Adapter<AdminTransactionAdapter.TxViewHolder> {

    private Context context;
    private List<Transaction> list;

    public AdminTransactionAdapter(Context context, List<Transaction> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public TxViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.admin_item_transactions, parent, false);
        return new TxViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TxViewHolder holder, int position) {
        Transaction tx = list.get(position);

        // 1. Transaction Code
        holder.tvCode.setText("#" + tx.getTransactionId());

        holder.tvTime.setText(" • " + tx.getCreatedAt());

        // 3. Amount
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String money = format.format(tx.getAmount());
        holder.tvAmount.setText("+" + money); // Assuming positive for list, adjust logic if needed
        holder.tvAmount.setTextColor(Color.parseColor("#16A34A")); // Green

        // 4. Users Flow (Sender -> Receiver)
        // If you only have Dest Name, you can show "Chuyển đến: [Name]"
        String dest = tx.getDestName() != null ? tx.getDestName() : "Unknown";
        holder.tvUsers.setText("Chuyển đến -> " + dest);

        // 5. Status Chip
        String status = tx.getStatus() != null ? tx.getStatus().toUpperCase() : "UNKNOWN";
        if (status.equals("COMPLETED") || status.equals("SUCCESS")) {
            holder.tvStatus.setText("Thành công");
            holder.tvStatus.setTextColor(Color.parseColor("#16A34A")); // Green text
            // holder.tvStatus.getBackground().setTint(Color.parseColor("#F0FDF4")); // Light green bg
        } else if (status.equals("FAILED")) {
            holder.tvStatus.setText("Thất bại");
            holder.tvStatus.setTextColor(Color.parseColor("#DC2626")); // Red text
            // holder.tvStatus.getBackground().setTint(Color.parseColor("#FEF2F2")); // Light red bg
        } else {
            holder.tvStatus.setText(status);
            holder.tvStatus.setTextColor(Color.GRAY);
        }

        // 6. Options Button Click
        holder.btnOptions.setOnClickListener(v -> {
            Toast.makeText(context, "Options for " + tx.getTransactionId(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class TxViewHolder extends RecyclerView.ViewHolder {
        TextView tvCode, tvTime, tvAmount, tvUsers, tvStatus;
        ImageView btnOptions;

        public TxViewHolder(@NonNull View itemView) {
            super(itemView);
            // Matching IDs from your new XML
            tvCode = itemView.findViewById(R.id.tv_trans_code);
            tvTime = itemView.findViewById(R.id.tv_trans_time);
            tvAmount = itemView.findViewById(R.id.tv_trans_amount);
            tvUsers = itemView.findViewById(R.id.tv_trans_users);
            tvStatus = itemView.findViewById(R.id.tv_trans_status);
            btnOptions = itemView.findViewById(R.id.btn_options);
        }
    }
}