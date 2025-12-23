package com.example.zybanking.ui.transaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.zybanking.R;
import com.example.zybanking.data.models.account.AccountSummaryResponse;
import com.example.zybanking.data.models.BasicResponse;
import com.example.zybanking.data.models.transaction.MortgagePaymentRequest;
import com.example.zybanking.data.models.auth.UserResponse;
import com.example.zybanking.data.remote.ApiService;
import com.example.zybanking.data.remote.RetrofitClient;
import com.example.zybanking.ui.dashboard.HomeActivity;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MortgagePaymentActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextView tvCheckingBalance, tvContractId, tvDueDate;
    private EditText edtAmount;
    private Button btnConfirm;

    private String mortgageAccountId;
    private double currentPaymentAmount = 0;
    private double checkingBalance = 0;
    private String checkingAccountId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mortgage_payment);

        mortgageAccountId = getIntent().getStringExtra("ACCOUNT_ID");

        initViews();
        loadData();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        tvCheckingBalance = findViewById(R.id.tv_checking_balance);
        tvContractId = findViewById(R.id.tv_contract_id);
        tvDueDate = findViewById(R.id.tv_due_date);
        edtAmount = findViewById(R.id.edt_amount);
        btnConfirm = findViewById(R.id.btn_confirm_payment);

        if (btnConfirm != null) btnConfirm.setOnClickListener(v -> handlePayment());
    }

    private void loadData() {
        ApiService api = RetrofitClient.getClient().create(ApiService.class);

        // 1. Lấy thông tin chi tiết khoản vay (Mortgage)
        if (mortgageAccountId != null) {
            tvContractId.setText(mortgageAccountId);
            api.getAccountSummary(mortgageAccountId).enqueue(new Callback<AccountSummaryResponse>() {
                @Override
                public void onResponse(Call<AccountSummaryResponse> call, Response<AccountSummaryResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                        AccountSummaryResponse.AccountData actualData = response.body().data;
                        if (actualData.paymentAmount != null) {
                            currentPaymentAmount = actualData.paymentAmount;
                            edtAmount.setText(formatCurrency(currentPaymentAmount).replace(" VND", ""));
                        }
                        if (actualData.nextPaymentDate != null) {
                            tvDueDate.setText(formatDate(actualData.nextPaymentDate));
                        }
                    }
                }
                @Override
                public void onFailure(Call<AccountSummaryResponse> call, Throwable t) {
                    Toast.makeText(MortgagePaymentActivity.this, "Lỗi tải thông tin vay", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // 2. Lấy thông tin tài khoản nguồn (Checking) để kiểm tra số dư
        SharedPreferences pref = getSharedPreferences("auth", MODE_PRIVATE);
        String token = pref.getString("access_token", "");

        api.getCurrentUser("Bearer " + token).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<Map<String, Object>> accounts = response.body().getData().getAccounts();
                    if (accounts != null) {
                        for (Map<String, Object> acc : accounts) {
                            String type = (String) acc.get("ACCOUNT_TYPE");
                            if ("CHECKING".equalsIgnoreCase(type)) {
                                String accId = (String) acc.get("ACCOUNT_ID");
                                fetchCheckingBalance(accId);
                                break;
                            }
                        }
                    }
                }
            }
            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {}
        });
    }

    private void fetchCheckingBalance(String accId) {
        this.checkingAccountId = accId;
        ApiService api = RetrofitClient.getClient().create(ApiService.class);
        api.getAccountSummary(accId).enqueue(new Callback<AccountSummaryResponse>() {
            @Override
            public void onResponse(Call<AccountSummaryResponse> call, Response<AccountSummaryResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                    AccountSummaryResponse.AccountData actualData = response.body().data;
                    if (actualData.balance != null) {
                        checkingBalance = actualData.balance;
                        tvCheckingBalance.setText("Số dư: " + formatCurrency(checkingBalance));
                    }
                }
            }
            @Override
            public void onFailure(Call<AccountSummaryResponse> call, Throwable t) {}
        });
    }

    private void handlePayment() {
        // Kiểm tra số dư trước khi tiến hành
        if (checkingBalance < currentPaymentAmount) {
            new AlertDialog.Builder(this)
                    .setTitle("Số dư không đủ")
                    .setMessage("Tài khoản thanh toán của bạn không đủ để trả khoản vay " + formatCurrency(currentPaymentAmount) + ".")
                    .setPositiveButton("Đóng", null)
                    .show();
            return;
        }

        // Hiển thị Dialog nhập PIN xác thực
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_pin_confirmation, null);
        EditText edtPin = dialogView.findViewById(R.id.edt_pin_code);

        new AlertDialog.Builder(this)
                .setTitle("Xác thực giao dịch")
                .setView(dialogView)
                .setPositiveButton("Xác nhận", (dialog, which) -> {
                    String pin = edtPin.getText().toString();
                    if (pin.length() == 6) {
                        executeTransaction(pin); // Chỉ gọi thực thi khi có mã PIN hợp lệ
                    } else {
                        Toast.makeText(this, "Mã PIN phải gồm 6 số", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void executeTransaction(String pin) {
        if (checkingAccountId == null || checkingAccountId.isEmpty()) {
            Toast.makeText(this, "Chưa xác định được tài khoản nguồn", Toast.LENGTH_SHORT).show();
            return;
        }

        btnConfirm.setEnabled(false);

        SharedPreferences pref = getSharedPreferences("auth", MODE_PRIVATE);
        String accessToken = pref.getString("access_token", "");
        ApiService api = RetrofitClient.getClient().create(ApiService.class);

        MortgagePaymentRequest request = new MortgagePaymentRequest(
                mortgageAccountId,
                currentPaymentAmount,
                checkingAccountId
        );

        api.createMortgagePayment("Bearer " + accessToken, request).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                btnConfirm.setEnabled(true); // Mở lại nút dù thành công hay thất bại

                // 1. Kiểm tra HTTP Code
                if (response.isSuccessful() && response.body() != null) {
                    BasicResponse body = response.body();

                    // 2. Kiểm tra LOGIC Status từ Backend
                    if ("success".equals(body.status)) {
                        // Kiểm tra xem transaction_id có bị null không
                        if (body.transaction_id != null) {
                            Intent intent = new Intent(MortgagePaymentActivity.this, MortgageOtpActivity.class);
                            intent.putExtra("transaction_id", body.transaction_id);
                            startActivity(intent);
                            // Log để kiểm tra
                            System.out.println("DEBUG ANDROID: Chuyển màn hình với ID: " + body.transaction_id);
                        } else {
                            Toast.makeText(MortgagePaymentActivity.this, "Lỗi: Không tìm thấy Transaction ID", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Backend trả về 200 nhưng status là error
                        Toast.makeText(MortgagePaymentActivity.this, body.message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Lỗi 400, 401, 500...
                    Toast.makeText(MortgagePaymentActivity.this, "Giao dịch thất bại (HTTP " + response.code() + ")", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                btnConfirm.setEnabled(true);
                Toast.makeText(MortgagePaymentActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                t.printStackTrace(); // Xem log lỗi chi tiết trong Logcat
            }
        });
    }

    private String formatCurrency(Double amount) {
        if (amount == null) return "0 VND";
        return NumberFormat.getInstance(new Locale("vi", "VN")).format(amount) + " VND";
    }

    private String formatDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) return "";
        try {
            // Lấy phần ngày yyyy-MM-dd
            return dateString.split(" ")[0];
        } catch (Exception e) {
            return dateString;
        }
    }
}