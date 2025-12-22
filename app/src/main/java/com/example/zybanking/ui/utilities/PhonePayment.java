package com.example.zybanking.ui.utilities;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.zybanking.R;
import com.example.zybanking.data.models.BasicResponse;
import com.example.zybanking.data.models.utility.UtilityConfirmRequest;
import com.example.zybanking.data.models.utility.UtilityRequest;
import com.example.zybanking.data.remote.ApiService;
import com.example.zybanking.data.remote.RetrofitClient;

import java.text.NumberFormat;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PhonePayment extends AppCompatActivity {

    private EditText etPhoneNumber;
    private Button btnConfirm;
    private ImageView btnBack;

    private double selectedAmount = 50000.0; // Mặc định 50k
    private Button lastSelectedButton = null;
    private String currentTxId;
    private String accountId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone_payment);

        // Lấy Account ID của người dùng từ bộ nhớ (đã lưu khi login)
        SharedPreferences pref = getSharedPreferences("auth", MODE_PRIVATE);
        accountId = pref.getString("ACCOUNT_ID", "ACC0001");

        initViews();
        setupAmountButtons();
    }

    private void initViews() {
        etPhoneNumber = findViewById(R.id.et_phone_number);
        btnConfirm = findViewById(R.id.btn_confirm_topup);
        btnBack = findViewById(R.id.btn_back_topup);

        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        btnConfirm.setOnClickListener(v -> {
            String phone = etPhoneNumber.getText().toString().trim();
            if (phone.length() < 10) {
                Toast.makeText(this, "Số điện thoại không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }
            handleTopupStep1(phone);
        });
    }

    private void setupAmountButtons() {
        // Ánh xạ ID các nút mệnh giá bạn đã thêm trong XML
        int[] ids = {R.id.btn_10k, R.id.btn_20k, R.id.btn_50k, R.id.btn_100k, R.id.btn_200k, R.id.btn_500k};
        double[] values = {10000, 20000, 50000, 100000, 200000, 500000};

        for (int i = 0; i < ids.length; i++) {
            Button btn = findViewById(ids[i]);
            double val = values[i];

            if (btn == null) continue;

            btn.setOnClickListener(v -> {
                // Trả màu nút cũ về trắng
                if (lastSelectedButton != null) {
                    lastSelectedButton.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                    lastSelectedButton.setTextColor(Color.parseColor("#2563EB"));
                }
                // Đổi nút mới sang màu xanh đậm của Banking
                btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#0b5394")));
                btn.setTextColor(Color.WHITE);

                selectedAmount = val;
                lastSelectedButton = btn;

                // Cập nhật text trên nút xác nhận chính
                btnConfirm.setText("Nạp ngay - " + formatVND(val));
            });

            // Giả lập click nút 50k làm mặc định
            if (val == 50000.0) btn.performClick();
        }
    }

    private void handleTopupStep1(String phone) {
        // Gửi yêu cầu nạp tiền lên Backend
        UtilityRequest request = new UtilityRequest(accountId, "TOPUP_SERVICE", phone, selectedAmount);
        ApiService api = RetrofitClient.getClient().create(ApiService.class);

        api.createUtilityPayment(request).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentTxId = response.body().transaction_id;
                    showOtpDialog(phone);
                } else {
                    Toast.makeText(PhonePayment.this, "Số dư không đủ hoặc lỗi hệ thống", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                Toast.makeText(PhonePayment.this, "Lỗi kết nối Server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showOtpDialog(String phone) {
        EditText etOtp = new EditText(this);
        etOtp.setHint("Nhập 6 số");
        etOtp.setTextAlignment(EditText.TEXT_ALIGNMENT_CENTER);
        etOtp.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);

        new AlertDialog.Builder(this)
                .setTitle("Xác thực OTP")
                .setMessage("Nhập mã OTP để nạp " + formatVND(selectedAmount) + " cho " + phone)
                .setView(etOtp)
                .setCancelable(false)
                .setPositiveButton("Xác nhận", (d, w) -> confirmOtp(etOtp.getText().toString()))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void confirmOtp(String code) {
        UtilityConfirmRequest req = new UtilityConfirmRequest(currentTxId, code);
        ApiService api = RetrofitClient.getClient().create(ApiService.class);

        api.confirmUtilityPayment(req).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                if (response.isSuccessful()) {
                    showSuccessMessage();
                } else {
                    Toast.makeText(PhonePayment.this, "OTP không chính xác", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {}
        });
    }

    private void showSuccessMessage() {
        new AlertDialog.Builder(this)
                .setTitle("Giao dịch thành công")
                .setMessage("Điện thoại đã được nạp tiền thành công.")
                .setPositiveButton("Về trang chủ", (d, w) -> finish())
                .show();
    }

    private String formatVND(double amount) {
        return NumberFormat.getCurrencyInstance(new Locale("vi", "VN")).format(amount);
    }
}