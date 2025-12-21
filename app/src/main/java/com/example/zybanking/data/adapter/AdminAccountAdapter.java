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
import com.example.zybanking.data.models.auth.User;

import java.util.List;

public class AdminAccountAdapter extends RecyclerView.Adapter<AdminAccountAdapter.UserViewHolder> {

    private Context context;
    private List<User> userList;
    private OnUserClickListener listener;
    public interface OnUserClickListener {
        void onUserClick(User user);
    }
    public AdminAccountAdapter(Context context, List<User> list, OnUserClickListener listener) {
        this.context = context;
        this.userList = userList;
        this.listener = listener;
    }
    public AdminAccountAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate your custom layout here
        View view = LayoutInflater.from(context).inflate(R.layout.admin_item_users, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);

        // 1. Name & ID
        holder.tvName.setText(user.getFullName() != null ? user.getFullName() : "No Name");
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onUserClick(user);
            }
        });
        holder.tvId.setText("ID: " + user.getUserId());

        // 2. Status Badge Logic
        if (user.isActive()) {
            holder.tvStatus.setText("Hoạt động");
            holder.tvStatus.setTextColor(Color.parseColor("#16A34A")); // Green text
            // If you have backgroundTint in XML, it works. If doing dynamically:
            // holder.tvStatus.getBackground().setTint(Color.parseColor("#F0FDF4"));
        } else {
            holder.tvStatus.setText("Đã khóa");
            holder.tvStatus.setTextColor(Color.parseColor("#DC2626")); // Red text
            // holder.tvStatus.getBackground().setTint(Color.parseColor("#FEF2F2"));
        }

        // 3. Role/Type Badge Logic
        // You requested "Customer" only, so we can display that or specific Account Type if you have that data
        String roleDisplay = user.getRole() != null ? user.getRole().toUpperCase() : "USER";
        holder.tvType.setText(roleDisplay);

        // 4. Click Event
        holder.itemView.setOnClickListener(v -> {
            Toast.makeText(context, "Selected: " + user.getFullName(), Toast.LENGTH_SHORT).show();
            // Intent to User Detail Activity can go here
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {

        // IDs matching your XML
        ImageView ivAvatar;
        TextView tvName, tvId, tvStatus, tvType;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            ivAvatar = itemView.findViewById(R.id.iv_employee_avatar);
            tvName = itemView.findViewById(R.id.tv_employee_name);
            tvId = itemView.findViewById(R.id.tv_employee_id);
            tvStatus = itemView.findViewById(R.id.tv_account_status);
            tvType = itemView.findViewById(R.id.tv_account_type);
        }
    }
}