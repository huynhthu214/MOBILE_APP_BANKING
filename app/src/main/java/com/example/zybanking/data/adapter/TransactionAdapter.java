    package com.example.zybanking.data.adapter;

    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.TextView;

    import androidx.recyclerview.widget.RecyclerView;

    import com.example.zybanking.R;
    import com.example.zybanking.data.models.Transaction;

    import java.text.NumberFormat;
    import java.util.List;
    import java.util.Locale;

    public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {
        private List<Transaction> transactions;
        public TransactionAdapter(List<Transaction> txns) { this.transactions = txns; }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_transaction_placeholder, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Transaction txn = transactions.get(position);
            holder.tvName.setText(txn.getType()); // hoặc txn.getDescription()
            holder.tvAmount.setText(formatCurrency(txn.getAmount()));
            holder.tvDate.setText(txn.getDate()); // nếu Transaction có field date
        }

        @Override
        public int getItemCount() { return transactions.size(); }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvName, tvDate, tvAmount;
            public ViewHolder(View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tv_trans_name);
                tvDate = itemView.findViewById(R.id.tv_trans_date);
                tvAmount = itemView.findViewById(R.id.tv_trans_amount);
            }
        }
        private String formatCurrency(double amount) {
            return NumberFormat.getInstance(new Locale("vi", "VN")).format(amount) + " VND";
        }
    }

