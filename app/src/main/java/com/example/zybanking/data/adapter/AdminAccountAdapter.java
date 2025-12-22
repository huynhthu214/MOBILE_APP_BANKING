package com.example.zybanking.data.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zybanking.R;
import com.example.zybanking.data.models.account.Account; // Đảm bảo dùng model Account
import com.example.zybanking.ui.account.AdminAccountDetailActivity;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class AdminAccountAdapter extends RecyclerView.Adapter<AdminAccountAdapter.AccountViewHolder> {

    private Context context;
    private List<Account> accountList;

    public AdminAccountAdapter(Context context, List<Account> accountList) {
        this.context = context;
        this.accountList = accountList;
    }

    @NonNull
    @Override
    public AccountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout item_admin_account_card mà chúng ta đã tạo ở bước trước
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_account_card, parent, false);
        return new AccountViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AccountViewHolder holder, int position) {
        Account account = accountList.get(position);

        // 1. Hiển thị Loại tài khoản và Số tài khoản
        holder.tvAccType.setText(account.getAccountType());
        holder.tvAccNumber.setText(account.getAccountNumber());
        holder.tvOwnerName.setText("Chủ thẻ: " + (account.getOwnerName() != null ? account.getOwnerName() : "N/A"));
        // 2. Định dạng số dư (VND)
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        holder.tvAccBalance.setText(formatter.format(account.getBalance()));
        // 3. Logic thay đổi Icon theo loại tài khoản (Nếu cần)
        if (account.getAccountType().equalsIgnoreCase("SAVING")) {
            holder.imgAccIcon.setImageResource(R.drawable.ic_card); // Bạn cần icon này
            holder.imgAccIcon.setColorFilter(Color.parseColor("#EAB308")); // Màu vàng cho tiết kiệm
        } else if (account.getAccountType().equalsIgnoreCase("MORTGAGE")) {
            holder.imgAccIcon.setImageResource(R.drawable.ic_home); // Icon cho vay
            holder.imgAccIcon.setColorFilter(Color.parseColor("#DC2626")); // Màu đỏ
        } else {
            holder.imgAccIcon.setImageResource(R.drawable.ic_card); // Mặc định là Checking
            holder.imgAccIcon.setColorFilter(Color.parseColor("#0b5394")); // Màu xanh bank
        }
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, AdminAccountDetailActivity.class);
            // Truyền ACCOUNT_ID để màn hình chi tiết gọi API
            intent.putExtra("ACCOUNT_ID", account.getAccountId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return accountList != null ? accountList.size() : 0;
    }

    public static class AccountViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAccIcon;
        TextView tvAccType, tvAccNumber, tvAccBalance, tvOwnerName;

        public AccountViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAccIcon = itemView.findViewById(R.id.img_acc_icon);
            tvAccType = itemView.findViewById(R.id.tv_acc_type);
            tvAccNumber = itemView.findViewById(R.id.tv_acc_number);
            tvAccBalance = itemView.findViewById(R.id.tv_acc_balance);
            tvOwnerName = itemView.findViewById(R.id.tvOwnerName);
        }
    }
}