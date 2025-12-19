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
import com.example.zybanking.data.models.Notification;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private Context context;
    private List<Notification> notificationList;

    // Định nghĩa các loại thông báo để dễ quản lý icon/màu sắc
    public static final int TYPE_TRANSACTION = 1;
    public static final int TYPE_SYSTEM = 2;
    public static final int TYPE_USER = 3;

    public NotificationAdapter(Context context, List<Notification> notificationList) {
        this.context = context;
        this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.admin_item_noti, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notification noti = notificationList.get(position);

        holder.tvTitle.setText(noti.getTitle());
        holder.tvDesc.setText(noti.getDescription());
        holder.tvTime.setText(noti.getTime());

        // 1. Xử lý Trạng thái Đã đọc / Chưa đọc
        if (noti.isRead()) {
            holder.viewDot.setVisibility(View.GONE); // Ẩn chấm đỏ
            holder.tvTitle.setTextColor(Color.parseColor("#4B5563")); // Màu xám nhạt hơn
        } else {
            holder.viewDot.setVisibility(View.VISIBLE); // Hiện chấm đỏ
            holder.tvTitle.setTextColor(Color.parseColor("#111827")); // Màu đen đậm
        }

        // 2. Xử lý Icon và Màu nền dựa theo Loại thông báo (Mock logic)
        switch (noti.getType()) {
            case TYPE_TRANSACTION:
                // Giao dịch: Icon Bill, Màu Xanh lá
                holder.imgIcon.setImageResource(R.drawable.ic_bill); // Đảm bảo bạn có icon này
                holder.imgIcon.setColorFilter(Color.parseColor("#16A34A"));
                holder.imgBg.setColorFilter(Color.parseColor("#F0FDF4")); // Nền xanh nhạt
                break;

            case TYPE_SYSTEM:
                // Hệ thống/Cảnh báo: Icon Info/Lock, Màu Đỏ/Cam
                holder.imgIcon.setImageResource(R.drawable.ic_lock); // Đảm bảo bạn có icon này
                holder.imgIcon.setColorFilter(Color.parseColor("#DC2626"));
                holder.imgBg.setColorFilter(Color.parseColor("#FEF2F2")); // Nền đỏ nhạt
                break;

            case TYPE_USER:
            default:
                // Người dùng: Icon User, Màu Xanh dương (Mặc định)
                holder.imgIcon.setImageResource(R.drawable.ic_user); // Đảm bảo bạn có icon này
                holder.imgIcon.setColorFilter(Color.parseColor("#2563EB"));
                holder.imgBg.setColorFilter(Color.parseColor("#EFF6FF")); // Nền xanh dương nhạt
                break;
        }
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDesc, tvTime;
        ImageView imgIcon, imgBg;
        View viewDot;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ánh xạ ID từ file item_notification_admin.xml
            tvTitle = itemView.findViewById(R.id.tv_noti_title);
            tvDesc = itemView.findViewById(R.id.tv_noti_desc);
            tvTime = itemView.findViewById(R.id.tv_noti_time);
            imgIcon = itemView.findViewById(R.id.img_noti_icon);
            imgBg = itemView.findViewById(R.id.img_noti_bg);
            viewDot = itemView.findViewById(R.id.view_unread_dot);
        }
    }
}