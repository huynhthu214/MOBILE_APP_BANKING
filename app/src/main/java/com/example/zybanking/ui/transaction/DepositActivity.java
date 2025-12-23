package com.example.zybanking.ui.transaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.zybanking.R;
import com.example.zybanking.data.models.transaction.PaymentResponse;
import com.example.zybanking.data.remote.ApiService;
import com.example.zybanking.data.remote.RetrofitClient;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DepositActivity extends AppCompatActivity {

    private static final String TAG = "DEBUG_DEPOSIT";

    private EditText etDepositAmount;
    private Button btnConfirmDeposit;
    private ImageView btnBack;
    private TextView tvSuggest100, tvSuggest200, tvSuggest500;

    private RadioButton rbVnpay, rbStripe;
    private CardView cardVnpay, cardStripe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deposit);

        bindViews();
        setupEvents();
        setupPaymentMethod();
    }

    // ================== INIT ==================

    private void bindViews() {
        btnBack = findViewById(R.id.btn_back_deposit);
        btnConfirmDeposit = findViewById(R.id.btn_confirm_deposit);
        etDepositAmount = findViewById(R.id.et_deposit_amount);

        tvSuggest100 = findViewById(R.id.tv_suggest_100);
        tvSuggest200 = findViewById(R.id.tv_suggest_200);
        tvSuggest500 = findViewById(R.id.tv_suggest_500);

        rbVnpay = findViewById(R.id.rb_vnpay);
        rbStripe = findViewById(R.id.rb_stripe);

        cardVnpay = findViewById(R.id.card_vnpay);
        cardStripe = findViewById(R.id.card_stripe);
    }

    private void setupEvents() {
        btnBack.setOnClickListener(v -> finish());

        btnConfirmDeposit.setOnClickListener(v -> handleDeposit());

        tvSuggest100.setOnClickListener(v -> updateAmount("100000"));
        tvSuggest200.setOnClickListener(v -> updateAmount("200000"));
        tvSuggest500.setOnClickListener(v -> updateAmount("500000"));
    }

    // ================== PAYMENT METHOD ==================

    private void setupPaymentMethod() {
        // Mặc định VNPay
        selectVnpay();

        cardVnpay.setOnClickListener(v -> selectVnpay());
        cardStripe.setOnClickListener(v -> selectStripe());
    }

    private void selectVnpay() {
        rbVnpay.setChecked(true);
        rbStripe.setChecked(false);
    }

    private void selectStripe() {
        rbStripe.setChecked(true);
        rbVnpay.setChecked(false);
    }

    // ================== LOGIC ==================

    private void updateAmount(String amount) {
        etDepositAmount.setText(amount);
        etDepositAmount.setSelection(amount.length());
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

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Số tiền không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences pref = getSharedPreferences("auth", MODE_PRIVATE);
        String accountId = pref.getString("main_account_id", "");

        if (accountId.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy tài khoản", Toast.LENGTH_SHORT).show();
            return;
        }

        String provider = rbVnpay.isChecked() ? "vnpay" : "stripe";

        Log.d(TAG, "Deposit: " + amount + " | provider=" + provider);

        Intent intent;

        if (provider.equals("vnpay")) {
            intent = new Intent(this, VnpayMockActivity.class);
        } else {
            intent = new Intent(this, StripeMockActivity.class);
        }

        intent.putExtra("account_id", accountId);
        intent.putExtra("amount", amount);
        intent.putExtra("provider", provider);

        startActivity(intent);

    }

    // ================== API ==================

    private void createPayment(String accountId, double amount, String provider) {

        ApiService api = RetrofitClient.getClient().create(ApiService.class);

        Map<String, Object> body = new HashMap<>();
        body.put("account_id", accountId);
        body.put("amount", amount);
        body.put("provider", provider);
        body.put("type", "DEPOSIT");

        api.createPayment(body).enqueue(new Callback<PaymentResponse>() {
            @Override
            public void onResponse(Call<PaymentResponse> call, Response<PaymentResponse> response) {

                if (response.isSuccessful() && response.body() != null) {

                    String paymentUrl = response.body().payment_url;
                    Log.d(TAG, "Payment URL: " + paymentUrl);

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
