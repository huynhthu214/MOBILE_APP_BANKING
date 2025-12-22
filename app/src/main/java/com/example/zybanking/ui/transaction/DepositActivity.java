package com.example.zybanking.ui.transaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.zybanking.R;
import com.example.zybanking.data.models.BasicResponse;
import com.example.zybanking.data.models.transaction.DepositRequest;
import com.example.zybanking.data.models.transaction.PaymentResponse;
import com.example.zybanking.data.remote.ApiService;
import com.example.zybanking.data.remote.RetrofitClient;
import com.example.zybanking.data.repository.TransactionRepository;
import com.google.gson.Gson; // Cần import Gson để soi JSON

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DepositActivity extends AppCompatActivity {

    private EditText etDepositAmount;
    private Button btnConfirmDeposit;
    private ImageView btnBack;
    private TextView tvSuggest100, tvSuggest200, tvSuggest500;

    private TransactionRepository repository;
    private RadioButton rbVnpay, rbStripe;


    // Tag để lọc log cho dễ
    private static final String TAG = "DEBUG_DEPOSIT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deposit);

        // ===== Bind view =====
        btnBack = findViewById(R.id.btn_back_deposit);
        btnConfirmDeposit = findViewById(R.id.btn_confirm_deposit);
        etDepositAmount = findViewById(R.id.et_deposit_amount);
        tvSuggest100 = findViewById(R.id.tv_suggest_100);
        tvSuggest200 = findViewById(R.id.tv_suggest_200);
        tvSuggest500 = findViewById(R.id.tv_suggest_500);
        rbVnpay = findViewById(R.id.rb_vnpay);
        rbStripe = findViewById(R.id.rb_stripe);

        repository = new TransactionRepository();

        btnBack.setOnClickListener(v -> finish());
        btnConfirmDeposit.setOnClickListener(v -> handleDeposit());
        tvSuggest100.setOnClickListener(v -> updateAmount("100000"));
        tvSuggest200.setOnClickListener(v -> updateAmount("200000"));
        tvSuggest500.setOnClickListener(v -> updateAmount("500000"));
    }

    private void updateAmount(String amount) {
        // Set text cho EditText
        etDepositAmount.setText(amount);

        // Di chuyển con trỏ chuột về cuối dòng (để user dễ nhập thêm nếu muốn)
        etDepositAmount.setSelection(etDepositAmount.getText().length());
    }
    private void handleDeposit() {
        String amountStr = etDepositAmount.getText().toString().trim();
        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập số tiền", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!rbVnpay.isChecked() && !rbStripe.isChecked()) {
            Toast.makeText(this, "Vui lòng chọn phương thức thanh toán", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);

        SharedPreferences pref = getSharedPreferences("auth", MODE_PRIVATE);
        String accountId = pref.getString("account_id", "");

        if (accountId.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy tài khoản", Toast.LENGTH_SHORT).show();
            return;
        }

        // 👇 Xác định provider
        String provider = rbVnpay.isChecked() ? "vnpay" : "stripe";

        createPayment(accountId, amount, provider);
    }

    private void createPayment(String accountId, double amount, String provider) {

        ApiService api = RetrofitClient.getClient().create(ApiService.class);

        Map<String, Object> body = new HashMap<>();
        body.put("account_id", accountId);
        body.put("amount", amount);
        body.put("provider", provider); // 👈 vnpay | stripe
        body.put("type", "DEPOSIT");

        api.createPayment(body).enqueue(new Callback<PaymentResponse>() {
            @Override
            public void onResponse(Call<PaymentResponse> call, Response<PaymentResponse> response) {

                if (response.isSuccessful() && response.body() != null) {

                    String paymentUrl = response.body().payment_url;
                    Log.e("CHECK_URL", "Link nhan duoc: " + paymentUrl);
                    Intent intent = new Intent(
                            DepositActivity.this,
                            PaymentWebViewActivity.class
                    );
                    intent.putExtra("url", paymentUrl);
                    startActivity(intent);

                } else {
                    Toast.makeText(
                            DepositActivity.this,
                            "Không tạo được giao dịch",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }

            @Override
            public void onFailure(Call<PaymentResponse> call, Throwable t) {
                Toast.makeText(
                        DepositActivity.this,
                        "Lỗi kết nối server",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

}