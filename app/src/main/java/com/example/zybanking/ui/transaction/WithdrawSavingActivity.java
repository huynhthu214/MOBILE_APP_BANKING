package com.example.zybanking.ui.transaction;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.example.zybanking.data.models.transaction.VerifyPinRequest;
import com.example.zybanking.data.models.transaction.WithdrawRequest;
import com.example.zybanking.data.remote.ApiService;
import com.example.zybanking.data.remote.RetrofitClient;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WithdrawSavingActivity extends AppCompatActivity {

    private ImageView btnBack;
    private EditText edtAmount;
    private TextView btnWithdrawAll, tvWarning;
    private Button btnConfirm;

    private String accountId;
    private Double currentBalance = 0.0;
    private ApiService apiService;
    private String mainAccountId = "";
    private double amountToWithdraw = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.savings_withdraw);
        // Lấy accountId được truyền từ màn hình trước đó
        mainAccountId = getIntent().getStringExtra("account_id");

        // Nếu màn hình trước không truyền, hãy lấy từ SharedPreferences (như bạn làm ở WithdrawActivity)
        if (mainAccountId == null || mainAccountId.isEmpty()) {
            SharedPreferences pref = getSharedPreferences("auth", MODE_PRIVATE);
            mainAccountId = pref.getString("main_account_id", "");
        }
        apiService = RetrofitClient.getClient().create(ApiService.class);
        accountId = getIntent().getStringExtra("ACCOUNT_ID");

        if (accountId == null) {
            Toast.makeText(this, "Không tìm thấy tài khoản", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupListeners();
        fetchAccountBalance();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        edtAmount = findViewById(R.id.edt_amount);
        btnWithdrawAll = findViewById(R.id.btn_withdraw_all);
        btnConfirm = findViewById(R.id.btn_confirm_withdraw);
        tvWarning = findViewById(R.id.tv_warning_text);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        // Handle "Withdraw All" (Tất toán toàn bộ)
        btnWithdrawAll.setOnClickListener(v -> {
            if (currentBalance > 0) {
                // Remove formatting for the EditText logic, set raw value
                edtAmount.setText(String.format(Locale.US, "%.0f", currentBalance));
                edtAmount.setSelection(edtAmount.getText().length()); // Move cursor to end
            } else {
                Toast.makeText(this, "Số dư hiện tại bằng 0", Toast.LENGTH_SHORT).show();
            }
        });

        btnConfirm.setOnClickListener(v -> handleWithdrawAttempt());
    }

    private void fetchAccountBalance() {
        // We reuse the account summary API to get the latest balance (Principal + Profit usually)
        apiService.getAccountSummary(accountId).enqueue(new Callback<AccountSummaryResponse>() {
            @Override
            public void onResponse(Call<AccountSummaryResponse> call, Response<AccountSummaryResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                    // Assuming 'balance' is the total withdrawable amount
                    currentBalance = response.body().data.balance;
                }
            }

            @Override
            public void onFailure(Call<AccountSummaryResponse> call, Throwable t) {
                Toast.makeText(WithdrawSavingActivity.this, "Không thể lấy thông tin số dư", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleWithdrawAttempt() {
        String amountStr = edtAmount.getText().toString().trim();

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

        if (amount > currentBalance) {
            Toast.makeText(this, "Số dư không đủ để thực hiện giao dịch", Toast.LENGTH_SHORT).show();
            return;
        }
        this.amountToWithdraw = Double.parseDouble(amountStr);
        showConfirmationDialog(this.amountToWithdraw);
    }

    // Trong WithdrawSavingActivity.java
    private void showConfirmationDialog(double amount) {
        // Giả định các thông số (Nên lấy từ API chi tiết sổ tiết kiệm)
        double currentRate = 4.5 / 100; // 4.5%
        double nonTermRate = 0.1 / 100; // 0.1% lãi không kỳ hạn

        // Tính toán số tiền lãi chênh lệch (ví dụ minh họa)
        double estimatedLoss = amount * (currentRate - nonTermRate);

        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        String formattedAmount = formatter.format(amount) + " VND";
        String formattedLoss = formatter.format(estimatedLoss) + " VND";

        new AlertDialog.Builder(this)
                .setTitle("Cảnh báo rút trước hạn")
                .setMessage("Nếu rút " + formattedAmount + " ngay bây giờ:\n" +
                        "- Lãi suất sẽ giảm từ 4.5% xuống 0.1%/năm.\n" +
                        "- Số tiền lãi dự kiến bị mất: ~" + formattedLoss + ".\n" +
                        "Bạn có chắc chắn muốn tiếp tục?")
                .setPositiveButton("Tiếp tục", (dialog, which) -> showPinDialog(amount))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showPinDialog(double amount) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_pin_confirmation, null);
        EditText edtPin = view.findViewById(R.id.edt_pin_code);

        builder.setView(view)
                .setPositiveButton("Xác nhận", (dialog, which) -> {
                    String pin = edtPin.getText().toString().trim();
                    if (pin.length() < 4) { // Kiểm tra độ dài PIN tùy logic của bạn
                        Toast.makeText(this, "Mã PIN không hợp lệ", Toast.LENGTH_SHORT).show();
                    } else {
                        executeWithdraw(amount, pin);
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void executeWithdraw(double amount, String pin) {
        btnConfirm.setEnabled(false);
        btnConfirm.setText("Đang xử lý...");

        // Gửi đúng request: accountId (Tài khoản chính nhận tiền), accountId (Sổ tiết kiệm nguồn)
        // Lưu ý: Đảm bảo constructor này khớp với file WithdrawRequest bạn đã sửa
        WithdrawRequest req = new WithdrawRequest(mainAccountId, accountId, amount, pin);

        // THAY ĐỔI: Gọi apiService.savingsWithdraw thay vì withdrawCreate
        apiService.savingsWithdrawCreate(req).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                btnConfirm.setEnabled(true);
                btnConfirm.setText("Xác nhận");

                if (response.isSuccessful() && response.body() != null) {
                    if ("success".equalsIgnoreCase(response.body().status)) {
                        // Vì đây là lệnh rút trực tiếp đã kèm PIN, bạn có thể chuyển thẳng sang OTP
                        // hoặc nếu Backend xử lý xong luôn thì quay về Home
                        Toast.makeText(WithdrawSavingActivity.this, "Yêu cầu đã được ghi nhận", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(WithdrawSavingActivity.this, WithdrawOtpActivity.class);
                        intent.putExtra("account_id", mainAccountId);
                        intent.putExtra("amount", amount);
                        // Nếu backend trả về txId thì truyền vào để xác thực OTP
                        intent.putExtra("transaction_id", response.body().transaction_id);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(WithdrawSavingActivity.this, response.body().message, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                btnConfirm.setEnabled(true);
                btnConfirm.setText("Xác nhận");
                Toast.makeText(WithdrawSavingActivity.this, "Lỗi kết nối server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void verifyPinForSaving(String txId, String pin, double amount) {
        VerifyPinRequest verifyReq = new VerifyPinRequest(txId, pin);

        apiService.verifyPin(verifyReq).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                if (response.isSuccessful() && response.body() != null && "success".equalsIgnoreCase(response.body().status)) {
                    Intent intent = new Intent(WithdrawSavingActivity.this, WithdrawOtpActivity.class);
                    intent.putExtra("transaction_id", txId);
                    intent.putExtra("amount", amount); // Bây giờ biến amount đã tồn tại ở đây
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(WithdrawSavingActivity.this, "Mã PIN không chính xác", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                btnConfirm.setEnabled(true);
                Toast.makeText(WithdrawSavingActivity.this, "Lỗi xác thực PIN", Toast.LENGTH_SHORT).show();
            }
        });
    }
}