package com.example.zybanking.ui.transaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.example.zybanking.data.models.UserResponse;
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
        btnBack.setOnClickListener(v -> finish());

        tvCheckingBalance = findViewById(R.id.tv_checking_balance);
        tvContractId = findViewById(R.id.tv_contract_id);
        tvDueDate = findViewById(R.id.tv_due_date);
        edtAmount = findViewById(R.id.edt_amount);
        btnConfirm = findViewById(R.id.btn_confirm_payment);

        btnConfirm.setOnClickListener(v -> handlePayment());
    }

    private void loadData() {
        ApiService api = RetrofitClient.getClient().create(ApiService.class);

        // 1. Lấy thông tin khoản vay (Mortgage) để điền số tiền và ngày hạn
        if (mortgageAccountId != null) {
            tvContractId.setText(mortgageAccountId);
            api.getAccountSummary(mortgageAccountId).enqueue(new Callback<AccountSummaryResponse>() {
                @Override
                public void onResponse(Call<AccountSummaryResponse> call, Response<AccountSummaryResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        AccountSummaryResponse data = response.body();
                        if (data.paymentAmount != null) {
                            currentPaymentAmount = data.paymentAmount;
                            // Hiển thị số tiền cần trả (bỏ format currency để hiển thị số thuần trong EditText nếu muốn, hoặc format)
                            // Ở đây EditText đang disable nên ta setText kiểu gì cũng được
                            edtAmount.setText(formatCurrency(currentPaymentAmount).replace(" VND", ""));
                        }
                        if (data.nextPaymentDate != null) {
                            tvDueDate.setText(formatDate(data.nextPaymentDate));
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
                if (response.isSuccessful() && response.body() != null) {
                    List<Map<String, Object>> accounts = response.body().getData().getAccounts();
                    if (accounts != null) {
                        for (Map<String, Object> acc : accounts) {
                            String type = (String) acc.get("ACCOUNT_TYPE");
                            if ("CHECKING".equalsIgnoreCase(type)) {
                                // Tìm thấy tài khoản thanh toán
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

    // Hàm phụ để lấy chi tiết số dư Checking
    private void fetchCheckingBalance(String accId) {
        ApiService api = RetrofitClient.getClient().create(ApiService.class);
        api.getAccountSummary(accId).enqueue(new Callback<AccountSummaryResponse>() {
            @Override
            public void onResponse(Call<AccountSummaryResponse> call, Response<AccountSummaryResponse> response) {
                if(response.isSuccessful() && response.body() != null) {
                    if(response.body().balance != null) {
                        checkingBalance = response.body().balance;
                        tvCheckingBalance.setText("Số dư: " + formatCurrency(checkingBalance));
                    }
                }
            }
            @Override
            public void onFailure(Call<AccountSummaryResponse> call, Throwable t) {}
        });
    }

    private void handlePayment() {
        // Validate
        if (checkingBalance < currentPaymentAmount) {
            new AlertDialog.Builder(this)
                    .setTitle("Số dư không đủ")
                    .setMessage("Tài khoản thanh toán của bạn không đủ để thực hiện giao dịch này.")
                    .setPositiveButton("Đóng", null)
                    .show();
            return;
        }

        // Xác nhận thanh toán
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
                    // Thành công
                    new AlertDialog.Builder(MortgagePaymentActivity.this)
                            .setTitle("Thành công")
                            .setMessage("Thanh toán khoản vay thành công!")
                            .setPositiveButton("Về trang chủ", (dialog, which) -> {
                                // Quay về Home để reload lại dữ liệu
                                Intent intent = new Intent(MortgagePaymentActivity.this, HomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            })
                            .setCancelable(false)
                            .show();
                } else {
                    Toast.makeText(MortgagePaymentActivity.this, "Thanh toán thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                Toast.makeText(MortgagePaymentActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // --- CÁC HÀM HELPER FORMAT ---
    private String formatCurrency(Double amount) {
        if (amount == null) return "0 VND";
        return NumberFormat.getInstance(new Locale("vi", "VN")).format(amount) + " VND";
    }

    private String formatDate(String dateString) {
        // Copy logic formatDate chuẩn Timezone từ HomeActivity sang đây
        // (Để code gọn mình không paste lại, bạn hãy copy hàm đó vào nhé)
        if (dateString == null) return "";
        // ... logic parse ...
        return dateString;
    }
}