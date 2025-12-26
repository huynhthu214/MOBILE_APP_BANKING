package com.example.zybanking.ui.utilities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.zybanking.R;
import com.example.zybanking.data.models.BasicResponse;
import com.example.zybanking.data.models.transaction.Bill;
import com.example.zybanking.data.models.transaction.BillResponse;
import com.example.zybanking.data.models.transaction.VerifyPinRequest;
import com.example.zybanking.data.models.utility.UtilityConfirmRequest;
import com.example.zybanking.data.models.utility.UtilityRequest;
import com.example.zybanking.data.remote.ApiService;
import com.example.zybanking.data.remote.RetrofitClient;
import com.example.zybanking.ui.dashboard.HomeActivity;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ElectricWaterPayment extends AppCompatActivity {

    private ImageView btnBack;
    private EditText etCustomerCode;
    private Spinner spinnerProvider;
    private Button btnCheckBill;

    private ApiService apiService;
    private String accountId;
    private String transactionId; // Lưu ID giao dịch để confirm OTP
    private double billAmount = 0; // Số tiền hóa đơn (Giả lập)
    private CardView cardResult;
    private TextView tvType, tvAmount, tvStatus, tvDueDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.electric_water_payment);

        apiService = RetrofitClient.getClient().create(ApiService.class);
        SharedPreferences pref = getSharedPreferences("auth", MODE_PRIVATE);
        accountId = pref.getString("ACCOUNT_ID", "ACC0001");

        cardResult = findViewById(R.id.card_bill_result);
        tvType = findViewById(R.id.tv_bill_type_name);
        tvAmount = findViewById(R.id.tv_bill_amount);
        tvStatus = findViewById(R.id.tv_bill_status);
        tvStatus = findViewById(R.id.tv_bill_due_date);

        initViews();
        setupData();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back_bill);
        etCustomerCode = findViewById(R.id.et_customer_code);
        spinnerProvider = findViewById(R.id.spinner_provider);
        btnCheckBill = findViewById(R.id.btn_check_bill);

        btnBack.setOnClickListener(v -> finish());

        // Bắt sự kiện tra cứu hóa đơn
        btnCheckBill.setOnClickListener(v -> handleCheckBill());
    }

    private void setupData() {
        // Setup Spinner nhà cung cấp (Hardcode mẫu)
        String[] providers = {"Điện lực Hà Nội (EVN)", "Điện lực TP.HCM", "Cấp nước Chợ Lớn", "Cấp nước Gia Định"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, providers);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProvider.setAdapter(adapter);
    }

    // --- BƯỚC 1: TRA CỨU HÓA ĐƠN THẬT ---
    private void handleCheckBill() {
        String billId = etCustomerCode.getText().toString().trim(); // Nhập B001, B002...
        if (billId.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập mã hóa đơn", Toast.LENGTH_SHORT).show();
            return;
        }

        // Gọi API tra cứu hóa đơn từ bảng BILL
        apiService.getBillDetail(billId).enqueue(new Callback<BillResponse>() {
            @Override
            public void onResponse(Call<BillResponse> call, Response<BillResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Bill bill = response.body().data;

                    // 1. Hiện Card kết quả
                    cardResult.setVisibility(View.VISIBLE);

                    // 2. Set tên dựa theo provider
                    if ("EVN".equalsIgnoreCase(bill.PROVIDER)) {
                        tvType.setText("Hóa đơn tiền điện (EVN)");
                    } else {
                        tvType.setText("Hóa đơn tiền nước");
                    }

                    // 3. Set tiền và ngày
                    tvAmount.setText(formatVND(bill.AMOUNT_DUE));
                    tvDueDate.setText(bill.DUE_DATE);

                    // 4. Set trạng thái tiếng Việt
                    if ("unpaid".equalsIgnoreCase(bill.STATUS)) {
                        tvStatus.setText("Chưa thanh toán");
                        tvStatus.setBackgroundResource(R.drawable.bg_status_unpaid);
                    } else {
                        tvStatus.setText("Đã thanh toán");
                        // Bạn có thể tạo thêm drawable màu xanh cho status "paid"
                    }
                } else {
                    Toast.makeText(ElectricWaterPayment.this, "Không tìm thấy hóa đơn!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BillResponse> call, Throwable t) {
                Log.e("API_ERROR", "Message: " + t.getMessage());
                String errorMsg = "Lỗi kết nối Server!";
                if (t instanceof java.net.SocketTimeoutException) {
                    errorMsg = "Kết nối quá hạn (Timeout), vui lòng thử lại!";
                } else if (t instanceof java.net.UnknownHostException) {
                    errorMsg = "Không có kết nối Internet!";
                }

                Toast.makeText(ElectricWaterPayment.this, errorMsg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showBillInfoDialog(Bill bill) {
        billAmount = bill.AMOUNT_DUE; // Lưu số tiền để thanh toán

        String message = "Loại: " + bill.BILL_TYPE +
                "\nNhà cung cấp: " + bill.PROVIDER +
                "\nSố tiền: " + formatVND(bill.AMOUNT_DUE) +
                "\nHạn thanh toán: " + bill.DUE_DATE +
                "\nTrạng thái: " + bill.STATUS;

        new AlertDialog.Builder(this)
                .setTitle("Thông tin hóa đơn")
                .setMessage(message)
                .setPositiveButton("Thanh toán ngay", (dialog, which) -> {
                    if ("PAID".equals(bill.STATUS)) {
                        Toast.makeText(this, "Hóa đơn này đã được thanh toán rồi", Toast.LENGTH_SHORT).show();
                    } else {
                        showPinDialog();
                    }
                })
                .setNegativeButton("Đóng", null)
                .show();
    }
    private void showPinDialog() {
        EditText etPin = new EditText(this);
        etPin.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        etPin.setHint("Nhập mã PIN 6 số");
        etPin.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        new AlertDialog.Builder(this)
                .setTitle("Xác thực PIN")
                .setView(etPin)
                .setPositiveButton("Xác nhận", (dialog, which) -> {
                    String pin = etPin.getText().toString();
                    verifyPin(pin);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void verifyPin(String pin) {
        // Gọi API check PIN
        VerifyPinRequest request = new VerifyPinRequest(accountId, pin);
        // Lưu ý: Bạn cần đảm bảo VerifyPinRequest có constructor hoặc setter phù hợp

        apiService.verifyPin(request).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                if (response.isSuccessful() && response.body() != null && "success".equals(response.body().status)) {
                    // PIN đúng -> Gọi API tạo giao dịch (Sẽ gửi OTP)
                    createPaymentAndSendOtp();
                } else {
                    Toast.makeText(ElectricWaterPayment.this, "Mã PIN không chính xác", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                Toast.makeText(ElectricWaterPayment.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // --- BƯỚC 3: TẠO GIAO DỊCH & GỬI OTP ---
    private void createPaymentAndSendOtp() {
        String billId = etCustomerCode.getText().toString(); // ID hóa đơn (ví dụ: B001)

        Map<String, String> params = new HashMap<>();
        params.put("bill_id", billId);
        params.put("account_id", accountId);

        apiService.createBillPayment(params).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    transactionId = response.body().transaction_id;
                    showOtpDialog(); // Hiện Dialog nhập OTP giống như bạn đã làm
                } else {
                    Toast.makeText(ElectricWaterPayment.this, "Lỗi: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                Toast.makeText(ElectricWaterPayment.this, "Lỗi kết nối server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // --- BƯỚC 4: NHẬP OTP & XÁC THỰC CUỐI ---
    private void showOtpDialog() {
        EditText etOtp = new EditText(this);
        etOtp.setInputType(InputType.TYPE_CLASS_NUMBER);
        etOtp.setHint("Nhập OTP từ email");
        etOtp.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        new AlertDialog.Builder(this)
                .setTitle("Nhập mã OTP")
                .setMessage("Mã OTP đã được gửi đến email đăng ký của bạn.")
                .setView(etOtp)
                .setCancelable(false)
                .setPositiveButton("Xác nhận", (dialog, which) -> {
                    String otp = etOtp.getText().toString();
                    confirmPayment(otp);
                })
                .show();
    }

    private void confirmPayment(String otp) {
        // Gọi API confirmUtilityPayment [cite: 313]
        UtilityConfirmRequest request = new UtilityConfirmRequest(transactionId, otp);

        apiService.confirmUtilityPayment(request).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                if (response.isSuccessful()) {
                    showSuccessDialog();
                } else {
                    Toast.makeText(ElectricWaterPayment.this, "OTP sai hoặc hết hạn", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                Toast.makeText(ElectricWaterPayment.this, "Lỗi xử lý", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showSuccessDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Thanh toán thành công")
                .setMessage("Đã thanh toán " + formatVND(billAmount) + " cho mã " + etCustomerCode.getText())
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton("Về trang chủ", (dialog, which) -> {
                    Intent intent = new Intent(ElectricWaterPayment.this, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .show();
    }

    private String formatVND(double amount) {
        return NumberFormat.getCurrencyInstance(new Locale("vi", "VN")).format(amount);
    }
}