package com.example.zybanking.data.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zybanking.R;
import com.example.zybanking.data.models.Notification;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotiViewHolder> {

    private List<Notification> list;

    public NotificationAdapter(List<Notification> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public NotiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new NotiViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotiViewHolder holder, int position) {
        // Lấy đối tượng từ danh sách 'list' đã khai báo ở trên
        Notification item = list.get(position);

        // Sử dụng các hàm Getter từ Model (đảm bảo Model của bạn đã có các hàm này)
        holder.tvTitle.setText(item.getTitle());
        holder.tvBody.setText(item.getBody());
        holder.tvTime.setText(item.getCreatedAt());

        // Xử lý chấm đỏ (Chưa đọc: IS_READ = 0)
        if (item.getIsRead() == 0) {
            holder.viewDot.setVisibility(View.VISIBLE);
            holder.tvTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        } else {
            holder.viewDot.setVisibility(View.GONE);
            holder.tvTitle.setTypeface(null, android.graphics.Typeface.NORMAL);
        }

// Xử lý Icon dựa theo cột TYPE trong Database của bạn
        int iconRes;
        String type = (item.getType() != null) ? item.getType() : "";

        switch (type) {
            case "TRANSACTION":
                iconRes = android.R.drawable.ic_menu_save; // Icon mặc định hệ thống
                break;
            case "SECURITY":
                iconRes = android.R.drawable.ic_lock_idle_lock;
                break;
            case "BILL":
                iconRes = android.R.drawable.ic_menu_agenda;
                break;
            case "PROMOTION":
                // Thay ic_promotion bằng icon hệ thống để hết lỗi đỏ
                iconRes = android.R.drawable.btn_star_big_on;
                break;
            case "SYSTEM":
                iconRes = android.R.drawable.ic_dialog_alert;
                break;
            default:
                iconRes = android.R.drawable.ic_dialog_info;
                break;
        }
        holder.imgIcon.setImageResource(iconRes);
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    static class NotiViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvBody, tvTime;
        ImageView imgIcon;
        View viewDot;

        public NotiViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_noti_title);
            tvBody = itemView.findViewById(R.id.tv_noti_body);
            tvTime = itemView.findViewById(R.id.tv_noti_time);
            imgIcon = itemView.findViewById(R.id.img_noti_icon);
            viewDot = itemView.findViewById(R.id.view_unread_dot);
        }
    }
}