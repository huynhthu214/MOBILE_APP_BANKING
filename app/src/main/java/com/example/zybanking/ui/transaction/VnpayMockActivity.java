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
import com.example.zybanking.data.models.BasicResponse;
import com.example.zybanking.data.models.transaction.DepositRequest;
import com.example.zybanking.data.models.transaction.DepositResponse;
import com.example.zybanking.data.remote.ApiService;
import com.example.zybanking.data.remote.RetrofitClient;
import com.example.zybanking.ui.dashboard.HomeActivity;

import java.text.NumberFormat;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VnpayMockActivity extends AppCompatActivity {

    private String accountId;
    private double amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mock_vnpay);

        TextView tvAmount = findViewById(R.id.tv_amount);
        TextView tvAccount = findViewById(R.id.tv_account);
        Button btnPay = findViewById(R.id.btn_pay);
        Button btnCancel = findViewById(R.id.btn_cancel);

        // Nhận dữ liệu từ DepositActivity
        accountId = getIntent().getStringExtra("account_id");
        amount = getIntent().getDoubleExtra("amount", 0);

        tvAccount.setText("Tài khoản: " + accountId);
        tvAmount.setText(formatCurrency(amount));

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

        DepositRequest request = new DepositRequest(
                accountId,
                amount,
                "VNPAY"
        );

        api.deposit("Bearer " + token, request)
                .enqueue(new Callback<DepositResponse>() {

                    @Override
                    public void onResponse(
                            Call<DepositResponse> call,
                            Response<DepositResponse> response
                    ) {
                        Log.e("DEPOSIT_DEBUG", "HTTP CODE = " + response.code());

                        if (response.errorBody() != null) {
                            try {
                                Log.e("DEPOSIT_DEBUG", "ERROR BODY = " + response.errorBody().string());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        Log.e("DEPOSIT_DEBUG", "BODY = " + response.body());

                        if (response.isSuccessful() && response.body() != null) {

                            DepositResponse data = response.body();

                            Log.d("DEPOSIT", "Transaction ID: " + data.transactionId);

                            Toast.makeText(VnpayMockActivity.this, data.message, Toast.LENGTH_LONG).show();

                            // --- SỬA ĐOẠN NÀY ---

                            // Thay vì về HomeActivity, hãy chuyển sang DepositOtpActivity
                            Intent intent = new Intent(VnpayMockActivity.this, DepositOtpActivity.class);

                            // Quan trọng: Phải truyền transaction_id sang để màn hình kia biết xác thực giao dịch nào
                            intent.putExtra("transaction_id", data.transactionId);

                            startActivity(intent);
                            finish(); // Đóng màn hình VNPAY Mock lại

                        } else {
                            Toast.makeText(
                                    VnpayMockActivity.this,
                                    "Nạp tiền thất bại",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<DepositResponse> call,
                            Throwable t
                    ) {
                        Toast.makeText(
                                VnpayMockActivity.this,
                                "Lỗi kết nối server",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });

    }

    private String formatCurrency(double amount) {
        return NumberFormat
                .getInstance(new Locale("vi", "VN"))
                .format(amount) + " VND";
    }
}
