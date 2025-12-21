package com.example.zybanking.ui.transaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.zybanking.R;
import com.example.zybanking.data.models.BasicResponse;
import com.example.zybanking.data.models.transaction.DepositRequest;
import com.example.zybanking.data.repository.TransactionRepository;
import com.google.gson.Gson; // Cần import Gson để soi JSON

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DepositActivity extends AppCompatActivity {

    private EditText etDepositAmount;
    private Button btnConfirmDeposit;
    private ImageView btnBack;
    private TextView tvSuggest100, tvSuggest200, tvSuggest500;

    private TransactionRepository repository;

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
        Log.d(TAG, "--- BẮT ĐẦU NẠP TIỀN ---");

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

        // ===== 1. LẤY account_id TỪ SESSION =====
        SharedPreferences pref = getSharedPreferences("auth", MODE_PRIVATE);
        String accountId = pref.getString("account_id", "");
        String token = pref.getString("access_token", "");

        // Log kiểm tra Session
        Log.d(TAG, "Token hiện có (4 ký tự đầu): " + (token.length() > 4 ? token.substring(0, 4) : "Rỗng"));
        Log.d(TAG, "Account ID lấy từ Pref: " + accountId);

        if (accountId.isEmpty()) {
            Toast.makeText(this, "Lỗi: Không tìm thấy ID tài khoản trong máy", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "LỖI: account_id bị rỗng -> Cần đăng xuất và login lại để lưu account_id");
            return;
        }

        // ===== 2. TẠO REQUEST & DEBUG JSON =====
        DepositRequest request = new DepositRequest(accountId, amount);

        // --- QUAN TRỌNG: In ra JSON thực tế sẽ gửi đi ---
        // Nếu ở đây in ra "accountId" (không gạch dưới) -> Lỗi 400
        // Nếu in ra "account_id" (có gạch dưới) -> JSON đúng
        String jsonDebug = new Gson().toJson(request);
        Log.e(TAG, ">>>>> JSON SẼ GỬI ĐI: " + jsonDebug);

        // ===== 3. GỌI API =====
        repository.createDeposit(
                DepositActivity.this,
                request,
                new Callback<BasicResponse>() {

                    @Override
                    public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                        Log.d(TAG, "HTTP Status Code: " + response.code());

                        if (!response.isSuccessful()) {
                            // --- XỬ LÝ LỖI (400, 401, 500) ---
                            String errorBody = "Không đọc được lỗi";
                            try {
                                if (response.errorBody() != null) {
                                    errorBody = response.errorBody().string();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            // In lỗi chi tiết từ server ra Logcat
                            Log.e(TAG, ">>>>> SERVER TRẢ VỀ LỖI: " + errorBody);

                            Toast.makeText(DepositActivity.this, "Lỗi " + response.code() + ": " + errorBody, Toast.LENGTH_LONG).show();
                            return;
                        }

                        // --- THÀNH CÔNG (200 OK) ---
                        BasicResponse body = response.body();
                        if (body == null) {
                            Log.e(TAG, "Body response bị null");
                            return;
                        }

                        Log.d(TAG, "Response Status: " + body.status);

                        if ("success".equalsIgnoreCase(body.status)) {
                            Log.d(TAG, "Tạo giao dịch thành công. ID: " + body.transaction_id);

                            // ===== CHUYỂN SANG OTP =====
                            Intent intent = new Intent(DepositActivity.this, DepositOtpActivity.class);
                            intent.putExtra("transaction_id", body.transaction_id);
                            intent.putExtra("account_id", accountId);
                            intent.putExtra("amount", amount);
                            startActivity(intent);

                        } else {
                            Log.e(TAG, "API báo thất bại: " + body.message);
                            Toast.makeText(DepositActivity.this, body.message, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<BasicResponse> call, Throwable t) {
                        Log.e(TAG, "Lỗi kết nối mạng: " + t.getMessage());
                        t.printStackTrace();
                        Toast.makeText(DepositActivity.this, "Lỗi mạng!", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }
}