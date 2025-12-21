package com.example.zybanking.ui.transaction;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import com.example.zybanking.R;
import com.example.zybanking.data.models.BasicResponse;
import com.example.zybanking.data.models.transaction.DepositRequest;
import com.example.zybanking.data.remote.ApiService;
import com.example.zybanking.data.remote.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DepositSavingActivity extends AppCompatActivity {

    private EditText edtAmount;
    private Button btnConfirm;
    private ImageView btnBack;
    private TextView tvTitle, tvSourceAccount;
    private String accountId;
    private String accountType; // Biến để phân biệt loại tài khoản

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.savings_deposit); // Sử dụng layout nạp tiền chung

        // Nhận dữ liệu từ Intent
        accountId = getIntent().getStringExtra("ACCOUNT_ID");
        accountType = getIntent().getStringExtra("ACCOUNT_TYPE"); // Nhận loại TK (checking/saving)

        initViews();
        setupUI();
    }

    private void initViews() {
        edtAmount = findViewById(R.id.edt_amount);
        btnConfirm = findViewById(R.id.btn_confirm_deposit);
        btnBack = findViewById(R.id.btnBack);
        tvTitle = findViewById(R.id.tv_title); // TextView tiêu đề ở header
        // Giả sử bạn có TextView hiển thị nguồn tiền
        // tvSourceAccount = findViewById(R.id.tv_source_account);

        btnBack.setOnClickListener(v -> finish());

        btnConfirm.setOnClickListener(v -> handleDeposit());
    }

    private void setupUI() {
        if ("saving".equalsIgnoreCase(accountType)) {
            tvTitle.setText("Gửi thêm tiết kiệm");
            // Có thể hiện thêm text: "Nguồn tiền: Tài khoản thanh toán"
        } else {
            tvTitle.setText("Nạp tiền vào tài khoản");
        }
    }

    private void handleDeposit() {
        String amountStr = edtAmount.getText().toString();
        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập số tiền", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);

        // --- BẮT ĐẦU ĐOẠN CODE LẤY TOKEN ---
        SharedPreferences sharedPreferences = getSharedPreferences("auth", MODE_PRIVATE);
        String token = sharedPreferences.getString("access_token", "");

        if (token.isEmpty()) {
            Toast.makeText(this, "Lỗi xác thực: Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            return;
        }
        // --- KẾT THÚC ĐOẠN CODE LẤY TOKEN ---

        ApiService api = RetrofitClient.getClient().create(ApiService.class);

        // Tạo request body
        DepositRequest request = new DepositRequest(accountId, amount);

        // GỌI API: Truyền "Bearer " + token vào tham số đầu tiên
        api.deposit("Bearer " + token, request).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(DepositSavingActivity.this, "Giao dịch thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(DepositSavingActivity.this, "Thất bại: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                Toast.makeText(DepositSavingActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}