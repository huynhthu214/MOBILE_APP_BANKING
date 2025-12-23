package com.example.zybanking.ui.transaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.zybanking.R;
import com.example.zybanking.data.models.transaction.DepositRequest;
import com.example.zybanking.data.models.transaction.DepositResponse;
import com.example.zybanking.data.remote.ApiService;
import com.example.zybanking.data.remote.RetrofitClient;

import java.text.NumberFormat;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StripeMockActivity extends AppCompatActivity {

    private String accountId;
    private double amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mock_stripe); // Đảm bảo layout đúng

        TextView tvAmount = findViewById(R.id.tv_amount);
        TextView tvCard = findViewById(R.id.tv_card);
        Button btnPay = findViewById(R.id.btn_pay);
        Button btnCancel = findViewById(R.id.btn_cancel);

        // 1. Nhận dữ liệu từ DepositActivity
        accountId = getIntent().getStringExtra("account_id");
        amount = getIntent().getDoubleExtra("amount", 0);

        tvCard.setText("Visa •••• 4242");
        tvAmount.setText(formatCurrency(amount));

        // 2. Sự kiện click nút Pay -> Gọi API
        btnPay.setOnClickListener(v -> callDepositApi());

        btnCancel.setOnClickListener(v -> finish());
    }

    private void callDepositApi() {
        SharedPreferences pref = getSharedPreferences("auth", MODE_PRIVATE);
        String token = pref.getString("access_token", "");

        if (token.isEmpty()) {
            Toast.makeText(this, "Mất phiên đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService api = RetrofitClient.getClient().create(ApiService.class);

        // Tạo request gửi lên server (Provider là STRIPE)
        DepositRequest request = new DepositRequest(
                accountId,
                amount,
                "STRIPE"
        );

        api.deposit("Bearer " + token, request)
                .enqueue(new Callback<DepositResponse>() {
                    @Override
                    public void onResponse(Call<DepositResponse> call, Response<DepositResponse> response) {
                        Log.d("STRIPE_DEBUG", "Code: " + response.code());

                        if (response.isSuccessful() && response.body() != null) {
                            DepositResponse data = response.body();

                            // 3. API thành công -> Chuyển sang màn hình nhập OTP
                            Toast.makeText(StripeMockActivity.this, "Đang chuyển đến xác thực OTP...", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(StripeMockActivity.this, DepositOtpActivity.class);

                            // QUAN TRỌNG: Truyền transaction_id sang màn hình OTP
                            intent.putExtra("transaction_id", data.transactionId);

                            startActivity(intent);
                            finish(); // Đóng màn hình Stripe giả lập lại

                        } else {
                            // Xử lý lỗi từ server
                            try {
                                String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                                Log.e("STRIPE_DEBUG", "Error: " + errorBody);
                                Toast.makeText(StripeMockActivity.this, "Lỗi server: " + response.code(), Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<DepositResponse> call, Throwable t) {
                        Toast.makeText(StripeMockActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("STRIPE_DEBUG", "Failure: " + t.getMessage());
                    }
                });
    }

    private String formatCurrency(double amount) {
        return NumberFormat.getInstance(new Locale("vi", "VN"))
                .format(amount) + " VND";
    }
}