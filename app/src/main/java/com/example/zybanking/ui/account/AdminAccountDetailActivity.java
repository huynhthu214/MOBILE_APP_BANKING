package com.example.zybanking.ui.account;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.zybanking.HeaderAdmin;
import com.example.zybanking.R;
import com.example.zybanking.data.models.BasicResponse;
import com.example.zybanking.data.models.account.Account;
import com.example.zybanking.data.models.account.AccountSummaryResponse;
import com.example.zybanking.data.remote.ApiService;
import com.example.zybanking.data.remote.RetrofitClient;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminAccountDetailActivity extends HeaderAdmin {
    private TextView tvType, tvNumber, tvBalance, tvBalanceLabel, tvOwner, tvStatus, tvRate;
    private Button btnLock;
    private String accountId, token;
    private ApiService apiService;
    private LinearLayout layoutRateRow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_account_detail);
        SharedPreferences pref = getSharedPreferences("auth", Context.MODE_PRIVATE);
        token = "Bearer " + pref.getString("auth_token", "");

        initHeader();
        initViews();

        accountId = getIntent().getStringExtra("ACCOUNT_ID");
        apiService = RetrofitClient.getClient().create(ApiService.class);
        if (accountId != null) {
            loadAccountDetail();
        }
    }

    private void initViews() {
        tvType = findViewById(R.id.tv_detail_type);
        tvNumber = findViewById(R.id.tv_detail_number);
        tvBalance = findViewById(R.id.tv_detail_balance);
        tvOwner = findViewById(R.id.tv_detail_owner);
        tvStatus = findViewById(R.id.tv_detail_status);
        tvRate = findViewById(R.id.tv_detail_rate);
        btnLock = findViewById(R.id.btn_lock_account);
        layoutRateRow = findViewById(R.id.layout_rate_row);
        // Giả sử bạn thêm ID này vào XML để đổi chữ "Số dư hiện tại"
        tvBalanceLabel = findViewById(R.id.tv_balance_label);

        btnLock.setOnClickListener(v -> lockAccount());
    }

    private void loadAccountDetail() {
        // Sử dụng API getAccountSummary để lấy dữ liệu chi tiết theo loại
        apiService.getAccountSummary(accountId).enqueue(new Callback<AccountSummaryResponse>() {
            @Override
            public void onResponse(Call<AccountSummaryResponse> call, Response<AccountSummaryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AccountSummaryResponse data = response.body();
                    displayData(data);
                }
            }

            @Override
            public void onFailure(Call<AccountSummaryResponse> call, Throwable t) {
                Toast.makeText(AdminAccountDetailActivity.this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayData(AccountSummaryResponse response) {
        if (response == null || response.data == null) return;
        AccountSummaryResponse.AccountData info = response.data;

        // 1. Set thông tin chung
        tvOwner.setText(info.ownerName != null ? info.ownerName : "Chưa cập nhật");
        tvNumber.setText(info.accountNumber);

        NumberFormat fmt = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        // 2. Xử lý hiển thị theo loại
        String type = info.type != null ? info.type.toLowerCase() : "checking";

        if (type.equals("checking")) {
            if(layoutRateRow != null) layoutRateRow.setVisibility(View.GONE);
            tvType.setText("TÀI KHOẢN THANH TOÁN");
            tvBalanceLabel.setText("Số dư khả dụng");
            tvBalance.setText(fmt.format(info.balance));
            tvRate.setText("");
        }
        else if (type.equals("saving")) {
            if(layoutRateRow != null) layoutRateRow.setVisibility(View.VISIBLE);
            tvType.setText("TIẾT KIỆM");
            tvBalanceLabel.setText("Số tiền gửi gốc");
            // Lấy từ principalAmount
            double amount = info.principalAmount != null ? info.principalAmount : info.balance;
            tvBalance.setText(fmt.format(amount));
            tvRate.setText(info.interestRate + "% / năm");
        }
        else if (type.equals("mortgage")) {
            if(layoutRateRow != null) layoutRateRow.setVisibility(View.VISIBLE);
            tvType.setText("KHOẢN VAY");
            tvBalanceLabel.setText("Dư nợ còn lại");
            // Lấy từ remainingBalance
            double amount = info.remainingBalance != null ? info.remainingBalance : 0;
            tvBalance.setText(fmt.format(amount));
            tvRate.setText(info.interestRate + "% / năm");
        }

        updateStatusUI(info.accountStatus);
    }
    private void updateStatusUI(String status) {
        if ("active".equalsIgnoreCase(status)) {
            tvStatus.setText("Đang hoạt động");
            tvStatus.setTextColor(Color.parseColor("#16A34A"));
            btnLock.setText("Khóa tài khoản");
            btnLock.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.RED));
        } else {
            tvStatus.setText("Ngưng hoạt động"); // Yêu cầu đổi thành tiếng Việt
            tvStatus.setTextColor(Color.GRAY);
            btnLock.setText("Kích hoạt lại");
            btnLock.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#16A34A")));
        }
    }

    private void lockAccount() {
        // Gọi API Update Account với status = unactive (hoặc ngược lại)
        String newStatus = tvStatus.getText().toString().equals("Đang hoạt động") ? "unactive" : "active";

        Map<String, Object> updateData = new HashMap<>();
        updateData.put("STATUS", newStatus);

        apiService.updateUser(accountId, updateData).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminAccountDetailActivity.this, "Cập nhật trạng thái thành công", Toast.LENGTH_SHORT).show();
                    updateStatusUI(newStatus);
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                Toast.makeText(AdminAccountDetailActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }
}