package com.example.zybanking.ui.utilities;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.zybanking.R;
import com.example.zybanking.data.models.BasicResponse;
import com.example.zybanking.data.models.utility.UtilityConfirmRequest;
import com.example.zybanking.data.models.utility.UtilityRequest;
import com.example.zybanking.data.remote.ApiService;
import com.example.zybanking.data.remote.RetrofitClient;

import java.text.NumberFormat;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UtilityPaymentActivity extends AppCompatActivity {
    // Views chung
    private Button btnConfirm;
    private String currentServiceType; // "PHONE", "ELECTRIC", "WATER"
    private String currentTxId;
    private String selectedAccountId;

    // Views cho Điện thoại
    private EditText etPhoneNumber;
    private double selectedAmount = 50000.0;
    private Button lastSelectedButton = null;

    // Views cho Điện/Nước
    private EditText etCustomerCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Lấy thông tin từ Intent và SharedPreferences
        currentServiceType = getIntent().getStringExtra("SERVICE_TYPE");
        SharedPreferences pref = getSharedPreferences("auth", MODE_PRIVATE);
        selectedAccountId = pref.getString("ACCOUNT_ID", "ACC0001");

        // 2. Quyết định nạp Layout nào
        if ("PHONE".equals(currentServiceType)) {
            setContentView(R.layout.phone_payment);
            initPhoneViews();
        } else {
            setContentView(R.layout.electric_water_payment);
            initUtilityViews();
        }
    }

    // --- LOGIC CHO ĐIỆN / NƯỚC ---
    private void initUtilityViews() {
        etCustomerCode = findViewById(R.id.et_customer_code);
        btnConfirm = findViewById(R.id.btn_check_bill);

        btnConfirm.setOnClickListener(v -> {
            String refNo = etCustomerCode.getText().toString().trim();
            if (refNo.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập mã khách hàng", Toast.LENGTH_SHORT).show();
                return;
            }
            // Giả sử số tiền cố định cho demo, thực tế nên có bước tra cứu (Lookup)
            handleInitialPayment(refNo, "UTILITY_PROVIDER", 250000.0);
        });

        findViewById(R.id.btn_back_bill).setOnClickListener(v -> finish());
    }

    // --- LOGIC CHO ĐIỆN THOẠI ---
    private void initPhoneViews() {
        etPhoneNumber = findViewById(R.id.et_phone_number);
        btnConfirm = findViewById(R.id.btn_confirm_topup);
        findViewById(R.id.btn_back_topup).setOnClickListener(v -> finish());

        setupAmountButtons();

        btnConfirm.setOnClickListener(v -> {
            String phone = etPhoneNumber.getText().toString().trim();
            if (phone.length() < 10) {
                Toast.makeText(this, "Số điện thoại không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }
            handleInitialPayment(phone, "VIETTEL", selectedAmount);
        });
    }

    private void setupAmountButtons() {
        int[] buttonIds = {R.id.btn_10k, R.id.btn_20k, R.id.btn_50k, R.id.btn_100k, R.id.btn_200k, R.id.btn_500k};
        double[] amounts = {10000, 20000, 50000, 100000, 200000, 500000};

        for (int i = 0; i < buttonIds.length; i++) {
            Button btn = findViewById(buttonIds[i]);
            double amount = amounts[i];
            if (btn == null) continue;

            btn.setOnClickListener(v -> {
                if (lastSelectedButton != null) {
                    lastSelectedButton.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                    lastSelectedButton.setTextColor(Color.parseColor("#2563EB"));
                }
                btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#0b5394")));
                btn.setTextColor(Color.WHITE);

                selectedAmount = amount;
                lastSelectedButton = btn;
                btnConfirm.setText("Nạp ngay - " + formatCurrency(amount));
            });
            // Mặc định chọn 50k
            if (amount == 50000.0) btn.performClick();
        }
    }

    // --- LOGIC GIAO DỊCH CHUNG ---
    private void handleInitialPayment(String refNo, String provider, Double amount) {
        UtilityRequest request = new UtilityRequest(selectedAccountId, provider, refNo, amount);
        ApiService api = RetrofitClient.getClient().create(ApiService.class);

        api.createUtilityPayment(request).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentTxId = response.body().transaction_id;
                    showOtpDialog();
                } else {
                    Toast.makeText(UtilityPaymentActivity.this, "Lỗi tạo giao dịch", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                Log.e("API", "Error: " + t.getMessage());
            }
        });
    }

    private void showOtpDialog() {
        EditText etOtp = new EditText(this);
        etOtp.setHint("Nhập mã OTP 6 số");
        etOtp.setPadding(50, 40, 50, 40);

        new AlertDialog.Builder(this)
                .setTitle("Xác thực OTP")
                .setMessage("Mã OTP đã được gửi để xác nhận thanh toán " + formatCurrency(selectedAmount))
                .setView(etOtp)
                .setCancelable(false)
                .setPositiveButton("Xác nhận", (dialog, which) -> confirmPaymentWithOtp(etOtp.getText().toString()))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void confirmPaymentWithOtp(String otpCode) {
        UtilityConfirmRequest confirmRequest = new UtilityConfirmRequest(currentTxId, otpCode);
        ApiService api = RetrofitClient.getClient().create(ApiService.class);

        api.confirmUtilityPayment(confirmRequest).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                if (response.isSuccessful()) {
                    new AlertDialog.Builder(UtilityPaymentActivity.this)
                            .setTitle("Thành công")
                            .setMessage("Thanh toán tiện ích hoàn tất!")
                            .setPositiveButton("OK", (d, w) -> finish())
                            .show();
                } else {
                    Toast.makeText(UtilityPaymentActivity.this, "OTP không hợp lệ", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {}
        });
    }

    private String formatCurrency(double amount) {
        return NumberFormat.getCurrencyInstance(new Locale("vi", "VN")).format(amount);
    }
}