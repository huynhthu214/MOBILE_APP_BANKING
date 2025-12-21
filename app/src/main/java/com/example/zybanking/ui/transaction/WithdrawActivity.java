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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.withdraw);

        // ===== 1. Ánh xạ View =====
        btnBack = findViewById(R.id.btn_back_withdraw);
        etAmount = findViewById(R.id.et_withdraw_amount);
        etNote = findViewById(R.id.et_note); // Ghi chú
        tvCurrentBalance = findViewById(R.id.tv_current_balance);
        btnConfirm = findViewById(R.id.btn_confirm_withdraw);

        apiService = RetrofitClient.getClient().create(ApiService.class);

        // ===== 2. Hiển thị số dư hiện tại (Optional) =====
        displayCurrentBalance();

        // ===== 3. Sự kiện Click =====
        btnBack.setOnClickListener(v -> finish());

        btnConfirm.setOnClickListener(v -> handleWithdrawCreate());
    }

    private void displayCurrentBalance() {
        // Lấy số dư từ SharedPreferences (hoặc bạn có thể truyền qua Intent từ HomeActivity)
        SharedPreferences pref = getSharedPreferences("auth", MODE_PRIVATE);
        // Lưu ý: Key "balance" này phải khớp với lúc bạn lưu ở Home/Login.
        // Nếu chưa lưu float/long, bạn có thể lưu String hoặc bỏ qua bước này.
        long balance = pref.getLong("balance_long", 0);

        // Format tiền tệ
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String formattedBalance = formatter.format(balance);
        tvCurrentBalance.setText(formattedBalance);
    }

    private void handleWithdrawCreate() {
        String amountStr = etAmount.getText().toString().trim();
        String note = etNote.getText().toString().trim(); // Lấy ghi chú

        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập số tiền", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Số tiền không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra số tiền tối thiểu (ví dụ 50k)
        if (amount < 50000) {
            Toast.makeText(this, "Số tiền rút tối thiểu là 50.000 VND", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lấy account_id
        SharedPreferences pref = getSharedPreferences("auth", MODE_PRIVATE);
        String accountId = pref.getString("account_id", "");

        if (accountId.isEmpty()) {
            Toast.makeText(this, "Lỗi phiên đăng nhập. Vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show();
            return;
        }

        btnConfirm.setEnabled(false);
        btnConfirm.setText("Đang xử lý...");

        // Tạo request
        // Nếu Backend chưa hỗ trợ note thì chỉ gửi accountId và amount
        WithdrawRequest req = new WithdrawRequest(accountId, amount);

        apiService.withdrawCreate(req).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                btnConfirm.setEnabled(true);
                btnConfirm.setText("Tiếp tục");

                if (response.isSuccessful() && response.body() != null) {
                    BasicResponse body = response.body();
                    if ("success".equals(body.status)) {
                        // Tạo lệnh thành công -> Sang màn nhập OTP
                        Intent intent = new Intent(WithdrawActivity.this, WithdrawOtpActivity.class);
                        intent.putExtra("transaction_id", body.transaction_id);
                        startActivity(intent);
                    } else {
                        Toast.makeText(WithdrawActivity.this, body.message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(WithdrawActivity.this, "Lỗi server: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                btnConfirm.setEnabled(true);
                btnConfirm.setText("Tiếp tục");
                Toast.makeText(WithdrawActivity.this, "Lỗi kết nối mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }
}