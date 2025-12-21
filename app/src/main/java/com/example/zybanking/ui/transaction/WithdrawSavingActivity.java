package com.example.zybanking.ui.transaction;

import android.app.AlertDialog;
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
import com.example.zybanking.data.models.transaction.WithdrawRequest;
import com.example.zybanking.data.remote.ApiService;
import com.example.zybanking.data.remote.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WithdrawSavingActivity extends AppCompatActivity {

    private EditText edtAmount;
    private Button btnConfirm;
    private ImageView btnBack;
    private TextView tvTitle, tvWarning;
    private String accountId;
    private String accountType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.savings_withdraw); // Layout rút tiền chung

        accountId = getIntent().getStringExtra("ACCOUNT_ID");
        accountType = getIntent().getStringExtra("ACCOUNT_TYPE");

        initViews();
        setupUI();
    }

    private void initViews() {
        edtAmount = findViewById(R.id.edt_amount);
        btnConfirm = findViewById(R.id.btn_confirm_withdraw);
        btnBack = findViewById(R.id.btnBack);
        tvTitle = findViewById(R.id.tv_title);

        // Bạn cần thêm TextView tv_warning vào layout activity_withdraw.xml (mặc định visibility="gone")
        tvWarning = findViewById(R.id.tv_warning_text);

        btnBack.setOnClickListener(v -> finish());

        btnConfirm.setOnClickListener(v -> {
            if ("saving".equalsIgnoreCase(accountType)) {
                showConfirmDialog(); // Cảnh báo trước khi rút
            } else {
                handleWithdraw();
            }
        });
    }

    private void setupUI() {
        if ("saving".equalsIgnoreCase(accountType)) {
            tvTitle.setText("Tất toán / Rút tiết kiệm");
            if (tvWarning != null) {
                tvWarning.setVisibility(View.VISIBLE);
                tvWarning.setText("Lưu ý: Rút trước hạn sẽ chỉ được hưởng lãi suất không kỳ hạn.");
            }
        } else {
            tvTitle.setText("Rút tiền");
            if (tvWarning != null) tvWarning.setVisibility(View.GONE);
        }
    }

    private void showConfirmDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận rút tiết kiệm")
                .setMessage("Bạn có chắc chắn muốn rút tiền từ sổ tiết kiệm này? Việc này có thể ảnh hưởng đến tiền lãi của bạn.")
                .setPositiveButton("Đồng ý", (dialog, which) -> handleWithdraw())
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void handleWithdraw() {
        String amountStr = edtAmount.getText().toString();
        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập số tiền", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);

        ApiService api = RetrofitClient.getClient().create(ApiService.class);
        WithdrawRequest request = new WithdrawRequest();
        request.setAccountId(accountId);
        request.setAmount(amount);

        api.withdrawCreate(request).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(WithdrawSavingActivity.this, "Giao dịch thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(WithdrawSavingActivity.this, "Giao dịch thất bại: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                Toast.makeText(WithdrawSavingActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }
}