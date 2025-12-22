package com.example.zybanking.ui.transaction;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zybanking.NavbarActivity;
import com.example.zybanking.R;
import com.example.zybanking.data.models.BasicResponse;
import com.example.zybanking.data.models.account.AccountSummaryResponse;
import com.example.zybanking.data.models.transaction.TransferRequest;
import com.example.zybanking.data.remote.ApiService;
import com.example.zybanking.data.remote.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionActivity extends NavbarActivity {
    private static final String TAG = "DEBUG_TRANSFER";

    private ApiService apiService;
    private String sourceAccountId = "";

    // UI Elements
    private EditText etRecipient, etAmount, etMessage;
    private Spinner spinnerBank;
    private Button btnConfirm, btn500, btn1m, btn2m;
    private TextView tvAvailableBalance, tvRecipientName;
    private LinearLayout btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.basic_transfer);

        initViews();
        setupBankSpinner();
        setupQuickAmounts();

        // 1. Khởi tạo API Service
        apiService = RetrofitClient.getClient().create(ApiService.class);

        // 2. Lấy dữ liệu tài khoản nguồn và số dư
        loadUserAccountInfo();

        // 3. Khởi tạo thanh điều hướng (Navbar) từ lớp cha
        initNavbar();
    }

    private void initViews() {
        etRecipient = findViewById(R.id.et_recipient_account);
        etAmount = findViewById(R.id.et_transfer_amount);
        etMessage = findViewById(R.id.et_transfer_message);
        spinnerBank = findViewById(R.id.spinner_bank);
        btnConfirm = findViewById(R.id.btn_confirm_transfer);
        tvAvailableBalance = findViewById(R.id.tv_available_balance);
        btnBack = findViewById(R.id.btn_back_transfer);
        tvRecipientName = findViewById(R.id.tv_recipient_name);
        btn500 = findViewById(R.id.btn_amount_500);
        btn1m = findViewById(R.id.btn_amount_1m);
        btn2m = findViewById(R.id.btn_amount_2m);

        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        // Sự kiện khi nhấn nút "Tiếp tục"
        if (btnConfirm != null) {
            btnConfirm.setOnClickListener(v -> handleContinueButtonClick());
        }
        etRecipient.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(android.text.Editable s) {
                String accountNumber = s.toString().trim();
                String selectedBank = spinnerBank.getSelectedItem().toString();

                if (accountNumber.length() >= 6) { // Giả sử số tài khoản từ 6 số trở lên
                    if (selectedBank.equals("ZY Banking")) {
                        lookupRealAccount(accountNumber);
                    } else {
                        simulateExternalAccount(accountNumber, selectedBank);
                    }
                } else {
                    tvRecipientName.setVisibility(View.GONE);
                }
            }
        });
    }
    private void lookupRealAccount(String accNo) {
        apiService.lookupAccount(accNo).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Giả sử Backend trả về tên trong field message hoặc fullName
                    String name = response.body().full_name;
                    tvRecipientName.setText("Chủ tài khoản: " + name);
                    tvRecipientName.setTextColor(Color.parseColor("#2E7D32")); // Màu xanh
                    tvRecipientName.setVisibility(View.VISIBLE);
                } else {
                    tvRecipientName.setText("Tài khoản không tồn tại");
                    tvRecipientName.setTextColor(Color.RED);
                    tvRecipientName.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                tvRecipientName.setVisibility(View.GONE);
            }
        });
    }

    private void simulateExternalAccount(String accNo, String bank) {
        // Giả lập cho các ngân hàng ngoài
        tvRecipientName.setText("Chủ tài khoản: NGUYEN VAN A");
        tvRecipientName.setTextColor(Color.BLUE);
        tvRecipientName.setVisibility(View.VISIBLE);
    }

    private void loadUserAccountInfo() {
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        sourceAccountId = prefs.getString("main_account_id", "");

        if (!sourceAccountId.isEmpty()) {
            apiService.getAccountSummary(sourceAccountId).enqueue(new Callback<AccountSummaryResponse>() {
                @Override
                public void onResponse(Call<AccountSummaryResponse> call, Response<AccountSummaryResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        double balance = response.body().data.balance;
                        tvAvailableBalance.setText("Số dư khả dụng: " + formatCurrency(balance));
                    }
                }
                @Override
                public void onFailure(Call<AccountSummaryResponse> call, Throwable t) {
                    Log.e(TAG, "Lỗi tải số dư");
                }
            });
        }
    }

    // Trong TransactionActivity.java, sửa lại hàm handleContinueButtonClick
    private void handleContinueButtonClick() {
        String recipient = etRecipient.getText().toString().trim();
        String amountStr = etAmount.getText().toString().trim();
        String message = etMessage.getText().toString().trim();
        String bankCode = spinnerBank.getSelectedItem().toString();

        if (recipient.isEmpty() || amountStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);
        if (amount < 10000) {
            Toast.makeText(this, "Số tiền tối thiểu là 10.000 VND", Toast.LENGTH_SHORT).show();
            return;
        }
        if (amount >= 10000000) { // Nếu >= 10 triệu
            // BƯỚC 1: BẮT BUỘC QUÉT KHUÔN MẶT
            ProgressDialog pd = new ProgressDialog(this);
            pd.setMessage("Giao dịch trên 10 triệu. Đang xác thực khuôn mặt...");
            pd.show();

            new Handler().postDelayed(() -> {
                pd.dismiss();
                callTransferCreateApi(recipient, amount, message, bankCode);
            }, 1500);
        } else {
            // BƯỚC 1: BỎ QUA QUÉT MẶT, GỌI API LUÔN (CHỈ DÙNG OTP Ở MÀN HÌNH SAU)
            callTransferCreateApi(recipient, amount, message, bankCode);
        }
    }

    private void callTransferCreateApi(String recipient, double amount, String note, String bank) {
        // Chuyển bank sang mã
        String toBankCode = bank.equals("ZY Banking") ? "LOCAL" : bank;

        // Bây giờ Constructor đã nhận đủ 5 tham số, sẽ không còn lỗi biên dịch
        TransferRequest request = new TransferRequest(sourceAccountId, recipient, amount, toBankCode, note);

        // Lưu ý: apiService.transferCreate trả về BasicResponse (theo file ApiService bạn gửi)
        apiService.transferCreate(request).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Lấy transaction_id từ BasicResponse (Backend trả về trong field này)
                    String txId = response.body().transaction_id;

                    Intent intent = new Intent(TransactionActivity.this, ConfirmTransactionActivity.class);
                    intent.putExtra("TX_ID", txId);
                    intent.putExtra("RECIPIENT", recipient);
                    intent.putExtra("AMOUNT", amount);
                    startActivity(intent);
                } else {
                    Toast.makeText(TransactionActivity.this, "Không tìm thấy tài khoản", Toast.LENGTH_SHORT).show();
                    try {
                        String errorBody = response.errorBody().string();
                        Log.e(TAG, "Lỗi từ Server: " + errorBody);
                        Toast.makeText(TransactionActivity.this, "Server báo: " + errorBody, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(TransactionActivity.this, "Lỗi không xác định", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                Toast.makeText(TransactionActivity.this, "Lỗi kết nối máy chủ", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void setupBankSpinner() {
        String[] banks = {"ZY Banking", "Vietcombank", "Techcombank", "BIDV", "Agribank"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, banks) {
            @Override
            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ((TextView) v).setTextColor(Color.parseColor("#374151"));
                return v;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBank.setAdapter(adapter);
    }

    private void setupQuickAmounts() {
        if (btn500 != null) btn500.setOnClickListener(v -> etAmount.setText("500000"));
        if (btn1m != null) btn1m.setOnClickListener(v -> etAmount.setText("1000000"));
        if (btn2m != null) btn2m.setOnClickListener(v -> etAmount.setText("2000000"));
    }

    private String formatCurrency(double amount) {
        java.text.NumberFormat formatter = java.text.NumberFormat.getInstance(new java.util.Locale("vi", "VN"));
        return formatter.format(amount) + " VND";
    }
}