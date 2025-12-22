package com.example.zybanking.ui.account;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_account_detail);

        initHeader();
        initViews();

        accountId = getIntent().getStringExtra("ACCOUNT_ID");
        apiService = RetrofitClient.getClient().create(ApiService.class);

        SharedPreferences pref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        token = "Bearer " + pref.getString("auth_token", "");

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
        // 1. Kiểm tra null an toàn
        if (response == null || response.data == null) {
            Toast.makeText(this, "Dữ liệu trống", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. Lấy object data bên trong ra
        AccountSummaryResponse.AccountData info = response.data;

        String type = (info.type != null) ? info.type.toLowerCase() : "checking";
        NumberFormat fmt = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        // 3. Cập nhật UI dựa trên biến 'info' (thay vì 'data.getType()')
        switch (type) {
            case "saving":
                tvType.setText("TÀI KHOẢN TIẾT KIỆM");
                tvBalanceLabel.setText("Số tiền tiết kiệm");
                // Dùng info.principalAmount, kiểm tra null nếu cần
                double principal = info.principalAmount != null ? info.principalAmount : 0.0;
                tvBalance.setText(fmt.format(principal));
                tvRate.setText(info.interestRate + "% / năm");
                break;

            case "mortgage":
                tvType.setText("TÀI KHOẢN THẾ CHẤP");
                tvBalanceLabel.setText("Thanh toán kỳ tới");
                double payment = info.paymentAmount != null ? info.paymentAmount : 0.0;
                tvBalance.setText(fmt.format(payment));
                tvRate.setText(info.interestRate + "% / năm");
                break;

            default: // checking
                tvType.setText("TÀI KHOẢN THANH TOÁN");
                tvBalanceLabel.setText("Số dư hiện tại");
                double balance = info.balance != null ? info.balance : 0.0;
                tvBalance.setText(fmt.format(balance));
                tvRate.setText("Không áp dụng");
                break;
        }

        // 4. Các thông tin chung
        tvNumber.setText(info.accountNumber);
        tvOwner.setText(info.ownerName);

        // Sửa status
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