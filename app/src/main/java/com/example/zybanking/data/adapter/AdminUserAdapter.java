package com.example.zybanking.data.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.zybanking.R;
import com.example.zybanking.data.models.auth.User;
import java.util.List;

public class AdminUserAdapter extends RecyclerView.Adapter<AdminUserAdapter.UserViewHolder> {
    private Context context;
    private List<User> userList;
    private OnUserClickListener listener;

    // Interface quan trọng để sửa lỗi "Cannot resolve symbol"
    public interface OnUserClickListener {
        void onUserClick(User user);
    }

    public AdminUserAdapter(Context context, List<User> userList, OnUserClickListener listener) {
        this.context = context;
        this.userList = userList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.admin_item_users, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.tvName.setText(user.getFullName());
        holder.tvId.setText("ID: " + user.getUserId());

        // Trạng thái hoạt động
        if (user.isActive()) {
            holder.tvStatus.setText("Hoạt động");
            holder.tvStatus.setTextColor(Color.parseColor("#16A34A"));
        } else {
            holder.tvStatus.setText("Đã khóa");
            holder.tvStatus.setTextColor(Color.parseColor("#DC2626"));
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null && user != null && user.getUserId() != null) {
                listener.onUserClick(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList != null ? userList.size() : 0;
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvId, tvStatus;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_employee_name);
            tvId = itemView.findViewById(R.id.tv_employee_id);
            tvStatus = itemView.findViewById(R.id.tv_account_status);
        }
    }
}