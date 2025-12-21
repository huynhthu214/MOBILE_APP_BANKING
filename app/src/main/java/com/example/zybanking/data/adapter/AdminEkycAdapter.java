package com.example.zybanking.data.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zybanking.R;
import com.example.zybanking.data.models.ekyc.EkycListResponse;

import java.util.List;

public class AdminEkycAdapter extends RecyclerView.Adapter<AdminEkycAdapter.EkycViewHolder> {

    private List<EkycListResponse.EkycItem> list;
    private final OnItemClickListener listener;

    // Interface chỉ cần 1 hành động: Click vào xem chi tiết
    public interface OnItemClickListener {
        void onClickDetail(EkycListResponse.EkycItem item);
    }

    public AdminEkycAdapter(List<EkycListResponse.EkycItem> list, OnItemClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    public void updateList(List<EkycListResponse.EkycItem> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EkycViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ekyc_review, parent, false);
        return new EkycViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EkycViewHolder holder, int position) {
        EkycListResponse.EkycItem item = list.get(position);

        holder.tvName.setText(item.fullName != null ? item.fullName : "Chưa cập nhật tên");
        holder.tvEmail.setText(item.email != null ? item.email : "No Email");

        // Sự kiện click nút "Chi tiết"
        holder.btnDetail.setOnClickListener(v -> listener.onClickDetail(item));

        // Hoặc click vào cả dòng cũng mở chi tiết (cho tiện)
        holder.itemView.setOnClickListener(v -> listener.onClickDetail(item));
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    static class EkycViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEmail;
        Button btnDetail;

        public EkycViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.item_tv_name);
            tvEmail = itemView.findViewById(R.id.item_tv_email);
            btnDetail = itemView.findViewById(R.id.item_btn_detail);
        }
    }
}