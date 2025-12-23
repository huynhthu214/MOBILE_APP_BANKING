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
import com.example.zybanking.data.models.transaction.VerifyPinRequest;
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

        if (amount > currentAvailableBalance) {
            Toast.makeText(this, "Số dư không đủ", Toast.LENGTH_SHORT).show();
            return;
        }

        if (amount < 50000) {
            Toast.makeText(this, "Số tiền rút tối thiểu là 50.000 VND", Toast.LENGTH_SHORT).show();
            return;
        }

        // Thay vì gọi API luôn, hãy hiện Dialog nhập PIN
        showPinDialog(amount);
    }

    private void showPinDialog(double amount) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        // Sử dụng layout dialog_pin_confirmation đã tạo ở bước trước
        android.view.View view = getLayoutInflater().inflate(R.layout.dialog_pin_confirmation, null);
        EditText edtPin = view.findViewById(R.id.edt_pin_code);

        builder.setView(view)
                .setPositiveButton("Xác nhận", (dialog, which) -> {
                    String pin = edtPin.getText().toString().trim();
                    if (pin.length() < 4) {
                        Toast.makeText(this, "Vui lòng nhập mã PIN hợp lệ", Toast.LENGTH_SHORT).show();
                    } else {
                        // Gọi hàm thực thi API với đầy đủ tham số
                        executeWithdrawApi(amount, pin);
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void executeWithdrawApi(double amount, String pin) {
        btnConfirm.setEnabled(false); // Vô hiệu hóa nút
        btnConfirm.setText("Đang khởi tạo...");

        // Sử dụng Constructor phù hợp cho rút tiền thường
        WithdrawRequest req = new WithdrawRequest(mainAccountId, amount, "ATM_WITHDRAW");

        apiService.withdrawCreate(req).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String txId = response.body().transaction_id;
                    verifyPinForWithdraw(txId, pin);
                } else {
                    resetConfirmButton();
                    handleErrorResponse(response);
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                resetConfirmButton();
                Toast.makeText(WithdrawActivity.this, "Lỗi kết nối Server", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void handleErrorResponse(Response<BasicResponse> response) {
        try {
            if (response.errorBody() != null) {
                String errorJson = response.errorBody().string();
                // Bạn có thể dùng Gson để parse errorJson lấy message nếu cần
                android.util.Log.e("API_ERROR", errorJson);
                Toast.makeText(this, "Lỗi từ hệ thống: " + response.code(), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void resetConfirmButton() {
        btnConfirm.setEnabled(true);
        btnConfirm.setText("Xác nhận");
    }
    private void verifyPinForWithdraw(String txId, String pin) {
        // Sử dụng VerifyPinRequest giống như bên ConfirmTransactionActivity
        VerifyPinRequest verifyRequest = new VerifyPinRequest(txId, pin);

        apiService.verifyPin(verifyRequest).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                if (response.isSuccessful()) {
                    // PIN ĐÚNG -> Chuyển sang màn hình OTP giống như luồng bạn đã làm
                    Toast.makeText(WithdrawActivity.this, "PIN chính xác!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(WithdrawActivity.this, WithdrawOtpActivity.class);
                    intent.putExtra("transaction_id", txId);
                    startActivity(intent);
                    finish();
                } else {
                    // PIN SAI
                    Toast.makeText(WithdrawActivity.this, "Mã PIN không chính xác", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                Toast.makeText(WithdrawActivity.this, "Lỗi xác thực PIN", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private String formatCurrency(Double amount) {
        if (amount == null) return "0 VND";
        return NumberFormat.getInstance(new Locale("vi", "VN")).format(amount) + " VND";
    }
}