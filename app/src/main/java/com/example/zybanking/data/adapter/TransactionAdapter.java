package com.example.zybanking.data.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zybanking.R;
import com.example.zybanking.data.models.TransactionHistoryItem;

import java.util.List;

public class TransactionAdapter
        extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    Context context;
    List<TransactionHistoryItem> list;

    public TransactionAdapter(Context context, List<TransactionHistoryItem> list) {
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
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        TransactionHistoryItem item = list.get(position);

        h.tvTitle.setText(item.title);
        h.tvTime.setText(item.time);

        if (item.isIncome) {
            h.tvAmount.setText("+ " + formatMoney(item.amount));
            h.tvAmount.setTextColor(context.getColor(R.color.green));
            h.icon.setImageResource(android.R.drawable.ic_input_add);
        } else {
            h.tvAmount.setText("- " + formatMoney(item.amount));
            h.tvAmount.setTextColor(context.getColor(R.color.red));
            h.icon.setImageResource(android.R.drawable.ic_delete);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

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

    private String formatMoney(double amount) {
        return String.format("%,.0fđ", amount);
    }
}
