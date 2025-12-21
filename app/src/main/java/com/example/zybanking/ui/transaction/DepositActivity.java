package com.example.zybanking.ui.transaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.zybanking.R;
import com.example.zybanking.data.models.BasicResponse;
import com.example.zybanking.data.models.DepositRequest;
import com.example.zybanking.data.repository.TransactionRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DepositActivity extends AppCompatActivity {

    private EditText etDepositAmount;
    private Button btnConfirmDeposit;
    private ImageView btnBack;

    private TransactionRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deposit);

        // ===== Bind view =====
        btnBack = findViewById(R.id.btn_back_deposit);
        btnConfirmDeposit = findViewById(R.id.btn_confirm_deposit);
        etDepositAmount = findViewById(R.id.et_deposit_amount);

        repository = new TransactionRepository();

        btnBack.setOnClickListener(v -> finish());
        btnConfirmDeposit.setOnClickListener(v -> handleDeposit());
    }

    private void handleDeposit() {

        String amountStr = etDepositAmount.getText().toString().trim();

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

        if (amount <= 0) {
            Toast.makeText(this, "Số tiền phải lớn hơn 0", Toast.LENGTH_SHORT).show();
            return;
        }

        // ===== LẤY account_id TỪ SESSION =====
        SharedPreferences pref = getSharedPreferences("auth", MODE_PRIVATE);
        String accountId = pref.getString("account_id", "");

        if (accountId.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy tài khoản", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("DEPOSIT", "accountId=" + accountId);
        Log.d("DEPOSIT", "amount=" + amount);

        DepositRequest request = new DepositRequest(accountId, amount);

        repository.createDeposit(
                DepositActivity.this,
                request,
                new Callback<BasicResponse>() {

                    @Override
                    public void onResponse(
                            Call<BasicResponse> call,
                            Response<BasicResponse> response
                    ) {
                        if (!response.isSuccessful()) {
                            Toast.makeText(
                                    DepositActivity.this,
                                    "HTTP lỗi: " + response.code(),
                                    Toast.LENGTH_SHORT
                            ).show();
                            return;
                        }

                        BasicResponse body = response.body();

                        if (body == null) {
                            Toast.makeText(
                                    DepositActivity.this,
                                    "Response rỗng",
                                    Toast.LENGTH_SHORT
                            ).show();
                            return;
                        }

                        if ("success".equalsIgnoreCase(body.status)) {

                            // ===== CHUYỂN SANG OTP =====
                            Intent intent = new Intent(
                                    DepositActivity.this,
                                    DepositOtpActivity.class
                            );
                            intent.putExtra("transaction_id", body.transaction_id);
                            intent.putExtra("account_id", accountId);
                            intent.putExtra("amount", amount);

                            startActivity(intent);

                        } else {
                            Toast.makeText(
                                    DepositActivity.this,
                                    body.message != null
                                            ? body.message
                                            : "Deposit thất bại",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<BasicResponse> call,
                            Throwable t
                    ) {
                        Toast.makeText(
                                DepositActivity.this,
                                "Không kết nối được backend",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
        );
    }
}
