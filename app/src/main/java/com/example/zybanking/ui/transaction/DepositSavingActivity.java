package com.example.zybanking.ui.transaction;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.zybanking.R;
import com.example.zybanking.data.models.BasicResponse;
import com.example.zybanking.data.models.account.AccountSummaryResponse;
import com.example.zybanking.data.models.transaction.DepositRequest;
import com.example.zybanking.data.remote.ApiService;
import com.example.zybanking.data.remote.RetrofitClient;

import java.text.NumberFormat;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DepositSavingActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextView tvSourceBalance;
    private EditText edtAmount;
    private Button btnConfirm;

    private String savingsAccountId;
    private ApiService apiService;
    // Assuming we fetch the default payment account balance to show "Source Balance"
    private Double sourceBalance = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.savings_deposit);

        apiService = RetrofitClient.getClient().create(ApiService.class);
        savingsAccountId = getIntent().getStringExtra("ACCOUNT_ID");

        initViews();
        fetchSourceAccountInfo(); // Get Payment Account Balance
        setupListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvSourceBalance = findViewById(R.id.tv_source_balance);
        edtAmount = findViewById(R.id.edt_amount);
        btnConfirm = findViewById(R.id.btn_confirm_deposit);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnConfirm.setOnClickListener(v -> {
            String amountStr = edtAmount.getText().toString().trim();
            if (validateInput(amountStr)) {
                double amount = Double.parseDouble(amountStr);
                confirmDeposit(amount);
            }
        });
    }

    private void fetchSourceAccountInfo() {
        SharedPreferences pref = getSharedPreferences("auth", MODE_PRIVATE);
        String mainAccountId = pref.getString("main_account_id", "");

        if (!mainAccountId.isEmpty()) {
            apiService.getAccountSummary(mainAccountId).enqueue(new Callback<AccountSummaryResponse>() {
                @Override
                public void onResponse(Call<AccountSummaryResponse> call, Response<AccountSummaryResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        sourceBalance = response.body().data.balance;
                        tvSourceBalance.setText("Số dư khả dụng: " + formatCurrency(sourceBalance));
                    }
                }

                @Override
                public void onFailure(Call<AccountSummaryResponse> call, Throwable t) {
                    tvSourceBalance.setText("Số dư khả dụng: Lỗi tải");
                }
            });
        }
    }

    private boolean validateInput(String amountStr) {
        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập số tiền", Toast.LENGTH_SHORT).show();
            return false;
        }
        try {
            double amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                Toast.makeText(this, "Số tiền phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                return false;
            }
            // Optional: Check if amount > sourceBalance
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Định dạng tiền không hợp lệ", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void confirmDeposit(double amount) {
        // Có thể hiện Dialog xác nhận số tiền trước, hoặc hiện thẳng Dialog PIN
        // Ở đây mình gộp: Hiện Dialog PIN luôn
        showPinDialog(amount);
    }

    private void showPinDialog(double amount) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_pin_confirmation, null);
        EditText edtPin = view.findViewById(R.id.edt_pin_code);

        builder.setView(view)
                .setPositiveButton("Gửi tiền", (dialog, which) -> {
                    String pin = edtPin.getText().toString().trim();
                    if (pin.isEmpty()) {
                        Toast.makeText(this, "Vui lòng nhập mã PIN", Toast.LENGTH_SHORT).show();
                    } else {
                        executeDeposit(amount, pin);
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void executeDeposit(double amount, String pin) {
        SharedPreferences pref = getSharedPreferences("auth", MODE_PRIVATE);
        String mainAccountId = pref.getString("main_account_id", "");

        // Gửi cả 2 ID: ID nguồn và ID sổ tiết kiệm
        DepositRequest request = new DepositRequest(mainAccountId, savingsAccountId, amount, pin);

        apiService.depositSavings(request).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        Toast.makeText(DepositSavingActivity.this, "Gửi thêm thành công!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(DepositSavingActivity.this, "Lỗi: " + response.body().message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(DepositSavingActivity.this, "Giao dịch thất bại (Sai PIN?)", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                Toast.makeText(DepositSavingActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String formatCurrency(Double amount) {
        if (amount == null) return "0 VND";
        return NumberFormat.getInstance(new Locale("vi", "VN")).format(amount) + " VND";
    }
}