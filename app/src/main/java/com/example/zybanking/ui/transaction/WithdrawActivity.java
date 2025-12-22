package com.example.zybanking.ui.transaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.zybanking.R;
import com.example.zybanking.data.models.BasicResponse;
import com.example.zybanking.data.models.account.AccountSummaryResponse;
import com.example.zybanking.data.models.transaction.WithdrawRequest;
import com.example.zybanking.data.remote.ApiService;
import com.example.zybanking.data.remote.RetrofitClient;

import java.text.NumberFormat;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WithdrawActivity extends AppCompatActivity {

    ImageView btnBack;
    EditText etAmount, etNote;
    TextView tvCurrentBalance;
    Button btnConfirm;

    ApiService apiService;
    private String mainAccountId = "";
    private double currentAvailableBalance = 0; // Lưu số dư để kiểm tra nhanh

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.withdraw);

        apiService = RetrofitClient.getClient().create(ApiService.class);
        initViews();

        // Load ID tài khoản chính đã lưu từ trang Home
        SharedPreferences pref = getSharedPreferences("auth", MODE_PRIVATE);
        mainAccountId = pref.getString("main_account_id", "");

        if (!mainAccountId.isEmpty()) {
            fetchAccountDetail(mainAccountId);
        } else {
            Toast.makeText(this, "Không tìm thấy thông tin tài khoản", Toast.LENGTH_SHORT).show();
        }

        btnBack.setOnClickListener(v -> finish());
        btnConfirm.setOnClickListener(v -> handleWithdrawCreate());
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back_withdraw);
        etAmount = findViewById(R.id.et_withdraw_amount);
        etNote = findViewById(R.id.et_note);
        tvCurrentBalance = findViewById(R.id.tv_current_balance);
        btnConfirm = findViewById(R.id.btn_confirm_withdraw);
    }

    // Hàm load dữ liệu từ API giống trang Home
    private void fetchAccountDetail(String accountId) {
        apiService.getAccountSummary(accountId).enqueue(new Callback<AccountSummaryResponse>() {
            @Override
            public void onResponse(Call<AccountSummaryResponse> call, Response<AccountSummaryResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                    currentAvailableBalance = response.body().data.balance;
                    tvCurrentBalance.setText(formatCurrency(currentAvailableBalance));
                }
            }
            @Override
            public void onFailure(Call<AccountSummaryResponse> call, Throwable t) {
                Toast.makeText(WithdrawActivity.this, "Không thể cập nhật số dư", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleWithdrawCreate() {
        String amountStr = etAmount.getText().toString().trim();
        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập số tiền", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);

        // Kiểm tra số dư khả dụng ngay tại máy trước khi gọi API
        if (amount > currentAvailableBalance) {
            Toast.makeText(this, "Số dư không đủ để thực hiện giao dịch", Toast.LENGTH_SHORT).show();
            return;
        }

        if (amount < 50000) {
            Toast.makeText(this, "Số tiền rút tối thiểu là 50.000 VND", Toast.LENGTH_SHORT).show();
            return;
        }

        btnConfirm.setEnabled(false);
        btnConfirm.setText("Đang xử lý...");

        WithdrawRequest req = new WithdrawRequest(mainAccountId, amount);
        apiService.withdrawCreate(req).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                btnConfirm.setEnabled(true);
                btnConfirm.setText("Tiếp tục");

                if (response.isSuccessful() && response.body() != null) {
                    BasicResponse body = response.body();
                    if ("success".equals(body.status)) {
                        Intent intent = new Intent(WithdrawActivity.this, WithdrawOtpActivity.class);
                        intent.putExtra("transaction_id", body.transaction_id);
                        startActivity(intent);
                    } else {
                        Toast.makeText(WithdrawActivity.this, body.message, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                btnConfirm.setEnabled(true);
                btnConfirm.setText("Tiếp tục");
                Toast.makeText(WithdrawActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String formatCurrency(Double amount) {
        if (amount == null) return "0 VND";
        return NumberFormat.getInstance(new Locale("vi", "VN")).format(amount) + " VND";
    }
}