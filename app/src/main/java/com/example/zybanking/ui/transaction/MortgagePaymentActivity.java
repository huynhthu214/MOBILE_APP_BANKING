package com.example.zybanking.ui.transaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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

        // 1. Lấy thông tin khoản vay (Mortgage)
        if (mortgageAccountId != null) {
            tvContractId.setText(mortgageAccountId);
            api.getAccountSummary(mortgageAccountId).enqueue(new Callback<AccountSummaryResponse>() {
                @Override
                public void onResponse(Call<AccountSummaryResponse> call, Response<AccountSummaryResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                        // SỬA TẠI ĐÂY: Lấy từ response.body().data
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

        // 2. Lấy thông tin tài khoản Checking để hiện số dư
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
        ApiService api = RetrofitClient.getClient().create(ApiService.class);
        api.getAccountSummary(accId).enqueue(new Callback<AccountSummaryResponse>() {
            @Override
            public void onResponse(Call<AccountSummaryResponse> call, Response<AccountSummaryResponse> response) {
                if(response.isSuccessful() && response.body() != null && response.body().data != null) {
                    // SỬA TẠI ĐÂY: Lấy balance từ data bên trong
                    AccountSummaryResponse.AccountData actualData = response.body().data;
                    if(actualData.balance != null) {
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
        if (checkingBalance < currentPaymentAmount) {
            new AlertDialog.Builder(this)
                    .setTitle("Số dư không đủ")
                    .setMessage("Tài khoản thanh toán của bạn không đủ (" + formatCurrency(checkingBalance) + ") để trả khoản vay " + formatCurrency(currentPaymentAmount) + ".")
                    .setPositiveButton("Đóng", null)
                    .show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Xác nhận thanh toán")
                .setMessage("Thanh toán " + formatCurrency(currentPaymentAmount) + " cho hợp đồng " + mortgageAccountId + "?")
                .setPositiveButton("Thanh toán", (dialog, which) -> executeTransaction())
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void executeTransaction() {
        ApiService api = RetrofitClient.getClient().create(ApiService.class);
        MortgagePaymentRequest request = new MortgagePaymentRequest(mortgageAccountId, currentPaymentAmount);

        api.payMortgage(request).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                if (response.isSuccessful()) {
                    showSuccessDialog();
                } else {
                    Toast.makeText(MortgagePaymentActivity.this, "Thanh toán thất bại: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                Toast.makeText(MortgagePaymentActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showSuccessDialog() {
        new AlertDialog.Builder(MortgagePaymentActivity.this)
                .setTitle("Thành công")
                .setMessage("Thanh toán khoản vay thành công!")
                .setPositiveButton("Về trang chủ", (dialog, which) -> {
                    Intent intent = new Intent(MortgagePaymentActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .setCancelable(false)
                .show();
    }

    private String formatCurrency(Double amount) {
        if (amount == null) return "0 VND";
        return NumberFormat.getInstance(new Locale("vi", "VN")).format(amount) + " VND";
    }

    private String formatDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) return "";
        try {
            // Logic đơn giản để parse, bạn nên dùng chung hàm parseDateString từ HomeActivity
            return dateString.split(" ")[0]; // Tạm thời lấy phần ngày nếu server gửi kèm giờ
        } catch (Exception e) {
            return dateString;
        }
    }
}