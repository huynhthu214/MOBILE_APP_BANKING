package com.example.zybanking.data.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zybanking.R;
import com.example.zybanking.data.models.Branch;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class BranchAdapter extends RecyclerView.Adapter<BranchAdapter.BranchViewHolder> {

    private List<Branch> branchList;
    private OnBranchClickListener listener;

    public interface OnBranchClickListener {
        void onBranchClick(Branch branch);
        void onDirectionClick(Branch branch);
    }

    public BranchAdapter(List<Branch> branchList, OnBranchClickListener listener) {
        this.branchList = branchList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BranchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_branch_location, parent, false);
        return new BranchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BranchViewHolder holder, int position) {
        Branch branch = branchList.get(position);
        holder.tvName.setText(branch.name);
        holder.tvAddress.setText(branch.address);

        // Hiển thị khoảng cách
        if (branch.distanceM != null) {
            if (branch.distanceM >= 1000) {
                holder.tvDistance.setText(String.format("Cách %.1f km", branch.distanceM / 1000));
            } else {
                holder.tvDistance.setText(String.format("Cách %.0f m", branch.distanceM));
            }
        } else {
            holder.tvDistance.setText("Đang tính toán...");
        }

        // Sự kiện click vào item
        holder.itemView.setOnClickListener(v -> listener.onBranchClick(branch));

        // Sự kiện click nút chỉ đường
        holder.btnDirections.setOnClickListener(v -> listener.onDirectionClick(branch));
    }

    @Override
    public int getItemCount() {
        return branchList != null ? branchList.size() : 0;
    }

    static class BranchViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvAddress, tvDistance;
        MaterialButton btnDirections;

        public BranchViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_branch_name);
            tvAddress = itemView.findViewById(R.id.tv_branch_address);
            tvDistance = itemView.findViewById(R.id.tv_branch_distance);
            btnDirections = itemView.findViewById(R.id.btn_directions);
        }
    }
}