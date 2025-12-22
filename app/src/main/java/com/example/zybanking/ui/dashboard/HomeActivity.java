package com.example.zybanking.ui.dashboard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Color;
import java.util.Date;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zybanking.NavbarActivity;
import com.example.zybanking.R;
import com.example.zybanking.data.adapter.TransactionAdapter;
import com.example.zybanking.data.models.account.AccountSummaryResponse;
import com.example.zybanking.data.models.auth.UserResponse;
import com.example.zybanking.data.remote.ApiService;
import com.example.zybanking.data.remote.RetrofitClient;
import com.example.zybanking.ui.ekyc.EkycActivity;
import com.example.zybanking.ui.map.MapActivity;
import com.example.zybanking.ui.transaction.DepositActivity;
import com.example.zybanking.ui.transaction.ElectricWaterPayment;
import com.example.zybanking.ui.transaction.MortgageActivity;
import com.example.zybanking.ui.transaction.PhonePayment;
import com.example.zybanking.ui.transaction.SavingsActivity;
import com.example.zybanking.ui.transaction.WithdrawActivity;
import com.example.zybanking.ui.utilities.UtilitiesActivity;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends NavbarActivity {
    // --- Khai báo biến UI ---
    private TextView tvUserName, tvBalance;
    private TextView tvSavingBalance, tvSavingRate, tvAccountNumber;
    private TextView tvMortgageAmount; //, tvNextPayment; (Biến này chưa dùng)
    private Button btnDeposit, btnWithdraw;
    private CardView cardSavings, cardMortgage, cardLocation, cardEKYC;
    private RecyclerView rvTransactions;
    private TextView btnViewMoreUtils;
    private LinearLayout btnElectricity, btnWater, btnPhone, btnTickets;

    // --- Biến UI mới thêm cho Saving/Mortgage ---
    private View layoutSavingInfo, layoutMortgageInfo;
    private TextView tvNoSaving, tvNoMortgage;
    private TextView tvMortgagePaymentAmount, tvMortgageDueDate, tvMortgageRemaining;

    // --- Biến xử lý ẩn/hiện số tài khoản chính ---
    private ImageView imgToggleAccountNo;
    private boolean isAccountHidden = true; // Trạng thái mặc định
    private String realAccountNumber = "";

    // --- BIẾN QUAN TRỌNG MỚI THÊM: Lưu ID tài khoản để truyền sang màn hình chi tiết ---
    private String savingAccountId = null;
    private String mortgageAccountId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.basic_home);

        initViews();
        setupNavigation();
        loadUserData();
        initNavbar();
    }

    private void initViews() {
        // Header
        tvUserName = findViewById(R.id.tv_user_name);
        tvBalance = findViewById(R.id.tv_balance);
        tvAccountNumber = findViewById(R.id.tv_account_number);

        // Saving UI
        tvSavingBalance = findViewById(R.id.tv_saving_balance);
        tvSavingRate = findViewById(R.id.tv_saving_rate);
        layoutSavingInfo = findViewById(R.id.layout_saving_info);
        tvNoSaving = findViewById(R.id.tv_no_saving);

        // Mortgage UI
        tvMortgageAmount = findViewById(R.id.tv_mortgage_payment_amount); // Lưu ý: Nếu trong XML bạn đã bỏ tv_mortgage_amount và thay bằng tv_mortgage_payment_amount thì dòng này có thể thừa hoặc null
        tvMortgagePaymentAmount = findViewById(R.id.tv_mortgage_payment_amount); // Đây là biến hiển thị số tiền cần trả
        tvMortgageDueDate = findViewById(R.id.tv_mortgage_due_date);
        tvMortgageRemaining = findViewById(R.id.tv_mortgage_remaining);
        layoutMortgageInfo = findViewById(R.id.layout_mortgage_info);
        tvNoMortgage = findViewById(R.id.tv_no_mortgage);

        // Buttons & Actions
        btnDeposit = findViewById(R.id.btn_deposit);
        btnWithdraw = findViewById(R.id.btn_withdraw);
        btnViewMoreUtils = findViewById(R.id.tv_view_more);
        btnElectricity = findViewById(R.id.btn_electricity);
        btnWater = findViewById(R.id.btn_water);
        btnPhone = findViewById(R.id.btn_phone);
        btnTickets = findViewById(R.id.btn_tickets);

        // Cards
        cardSavings = findViewById(R.id.card_savings);
        cardMortgage = findViewById(R.id.card_mortgage);
        cardLocation = findViewById(R.id.card_location);
        cardEKYC = findViewById(R.id.card_ekyc);

        rvTransactions = findViewById(R.id.rv_transactions);

        // Toggle Account Number
        imgToggleAccountNo = findViewById(R.id.img_toggle_account_no);
        if (imgToggleAccountNo != null) {
            imgToggleAccountNo.setOnClickListener(v -> toggleAccountNumber());
        }
    }
    private void setupNavigation() {
        if(btnDeposit != null) btnDeposit.setOnClickListener(v -> startActivity(new Intent(this, DepositActivity.class)));
        if(btnWithdraw != null) btnWithdraw.setOnClickListener(v -> startActivity(new Intent(this, WithdrawActivity.class)));

        View.OnClickListener utilListener = v -> startActivity(new Intent(this, UtilitiesActivity.class));
        if (btnViewMoreUtils != null) btnViewMoreUtils.setOnClickListener(utilListener);
        if (btnElectricity != null) btnElectricity.setOnClickListener(v -> startActivity(new Intent(this, ElectricWaterPayment.class)));
        if (btnWater != null) btnWater.setOnClickListener(v -> startActivity(new Intent(this, ElectricWaterPayment.class)));
        if (btnPhone != null) btnPhone.setOnClickListener(v -> startActivity(new Intent(this, PhonePayment.class)));
        if (btnTickets != null) btnTickets.setOnClickListener(v -> startActivity(new Intent(this, UtilitiesActivity.class)));

        // --- SỬA LOGIC CLICK VÀO CARD SAVING ---
        if(cardSavings != null) {
            cardSavings.setOnClickListener(v -> {
                if (savingAccountId != null) {
                    Intent intent = new Intent(HomeActivity.this, SavingsActivity.class);
                    intent.putExtra("ACCOUNT_ID", savingAccountId);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Bạn chưa có tài khoản tiết kiệm", Toast.LENGTH_SHORT).show();
                }
            });
        }
        // --- SỬA LOGIC CLICK VÀO CARD MORTGAGE ---
        if(cardMortgage != null) {
            cardMortgage.setOnClickListener(v -> {
                if (mortgageAccountId != null) {
                    Intent intent = new Intent(HomeActivity.this, MortgageActivity.class);
                    intent.putExtra("ACCOUNT_ID", mortgageAccountId);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Bạn chưa có khoản vay thế chấp", Toast.LENGTH_SHORT).show();
                }
            });
        }
        if(cardLocation != null) cardLocation.setOnClickListener(v -> startActivity(new Intent(this, MapActivity.class)));
        if(cardEKYC != null) cardEKYC.setOnClickListener(v -> startActivity(new Intent(this, EkycActivity.class)));
    }
    private void loadUserData() {
        String token = "";
        if (getIntent().hasExtra("EXTRA_TOKEN")) {
            token = getIntent().getStringExtra("EXTRA_TOKEN");
        }
        if (token == null || token.isEmpty()) {
            SharedPreferences pref = getSharedPreferences("auth", MODE_PRIVATE);
            token = pref.getString("access_token", "");
        }
        if(token.isEmpty()) return;

        ApiService api = RetrofitClient.getClient().create(ApiService.class);
        api.getCurrentUser("Bearer " + token).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if(response.isSuccessful() && response.body() != null) {
                    UserResponse.User user = response.body().getData().getUser();
                    if (tvUserName != null) tvUserName.setText(user.getFullName());

                    List<Map<String, Object>> accounts = response.body().getData().getAccounts();
                    if (accounts != null) {
                        for (Map<String, Object> acc : accounts) {
                            Log.d("DEBUG_HOME", "Raw Account Data: " + acc.toString());

                            // 1. Lấy Tài khoản chính (Checking)
                            String mainAccId = null;
                            if (acc.containsKey("ACCOUNT_ID")) mainAccId = String.valueOf(acc.get("ACCOUNT_ID"));
                            else if (acc.containsKey("account_id")) mainAccId = String.valueOf(acc.get("account_id"));

                            if (mainAccId != null) {
                                Log.d("DEBUG_HOME", ">>> Fetching Main ID: " + mainAccId);
                                fetchAccountDetail(mainAccId);
                            }

                            // 2. Lấy Tài khoản Tiết kiệm (Saving)
                            if (acc.containsKey("SAVING_ACC_ID") && acc.get("SAVING_ACC_ID") != null) {
                                String savingId = String.valueOf(acc.get("SAVING_ACC_ID"));
                                Log.d("DEBUG_HOME", ">>> Fetching Saving ID: " + savingId);
                                fetchAccountDetail(savingId);
                            }

                            // 3. Lấy Tài khoản Vay (Mortgage)
                            if (acc.containsKey("MORTAGE_ACC_ID") && acc.get("MORTAGE_ACC_ID") != null) {
                                String mortgageId = String.valueOf(acc.get("MORTAGE_ACC_ID"));
                                Log.d("DEBUG_HOME", ">>> Fetching Mortgage ID: " + mortgageId);
                                fetchAccountDetail(mortgageId);
                            }
                        }
                    }
                } else {
                    Log.e("DEBUG_HOME", "Get User Failed: " + response.code());
                }
            }
            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Log.e("DEBUG_HOME", "User API Error: " + t.getMessage());
            }
        });
    }
    private void fetchAccountDetail(String accountId) {
        ApiService api = RetrofitClient.getClient().create(ApiService.class);
        api.getAccountSummary(accountId).enqueue(new Callback<AccountSummaryResponse>() {
            @Override
            public void onResponse(Call<AccountSummaryResponse> call, Response<AccountSummaryResponse> response) {
                // Log xem server trả về mã bao nhiêu (200 là ok, 404/500 là lỗi)
                Log.d("DEBUG_HOME", "API Summary for " + accountId + " Code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    // Log xem Type server trả về là gì
                    updateUI(response.body(), accountId);
                } else {
                    // Nếu lỗi, in ra lỗi
                    try {
                        Log.e("DEBUG_HOME", "Error Body: " + response.errorBody().string());
                    } catch (Exception e) {}
                }
            }

            @Override
            public void onFailure(Call<AccountSummaryResponse> call, Throwable t) {
                Log.e("DEBUG_HOME", "Fetch Detail Failed: " + t.getMessage());
            }
        });
    }
    private void updateUI(AccountSummaryResponse response, String currentAccId) {
        // 1. Kiểm tra an toàn dữ liệu đầu vào
        if (response == null || response.data == null) {
            Log.e("DEBUG_HOME", "updateUI: Response hoặc Data bị NULL");
            return;
        }

        // 2. Trích xuất object data bên trong (theo đúng cấu trúc JSON bạn cung cấp)
        AccountSummaryResponse.AccountData actualData = response.data;

        if (actualData.type == null) {
            Log.e("DEBUG_HOME", "updateUI: Type của tài khoản bị NULL");
            return;
        }

        String accountType = actualData.type.toUpperCase().trim();
        Log.d("DEBUG_HOME", "Đang cập nhật UI cho Type: " + accountType + " | ID: " + currentAccId);

        // 3. Xử lý hiển thị dựa trên loại tài khoản
        switch (accountType) {
            case "CHECKING":
                // Hiển thị số tài khoản chính
                if (actualData.accountNumber != null) {
                    realAccountNumber = actualData.accountNumber;
                    updateAccountNumberDisplay();
                }

                // Hiển thị số dư chính
                if (tvBalance != null) {
                    tvBalance.setText(formatCurrency(actualData.balance));
                }

                // Cập nhật danh sách giao dịch gần đây
                if (actualData.lastTransactions != null && rvTransactions != null) {
                    TransactionAdapter adapter = new TransactionAdapter(HomeActivity.this, actualData.lastTransactions);
                    rvTransactions.setAdapter(adapter);
                    Log.d("DEBUG_HOME", "Đã cập nhật " + actualData.lastTransactions.size() + " giao dịch.");
                }
                break;

            case "SAVING":
                // Lưu lại ID để nhấn vào Card có thể chuyển sang trang chi tiết
                savingAccountId = currentAccId;

                // Ẩn dòng thông báo "Chưa có" và hiện Layout thông tin
                if (tvNoSaving != null) tvNoSaving.setVisibility(View.GONE);
                if (layoutSavingInfo != null) layoutSavingInfo.setVisibility(View.VISIBLE);

                // Đổ dữ liệu tiết kiệm
                if (tvSavingBalance != null) {
                    tvSavingBalance.setText(formatCurrency(actualData.balance));
                }
                if (tvSavingRate != null && actualData.interestRate != null) {
                    tvSavingRate.setText("Lãi suất: " + (actualData.interestRate * 100) + "% / năm");
                }
                break;

            case "MORTGAGE":
                // Lưu lại ID để chuyển sang trang chi tiết khoản vay
                mortgageAccountId = currentAccId;

                // Ẩn dòng thông báo "Chưa có" và hiện Layout thông tin
                if (tvNoMortgage != null) tvNoMortgage.setVisibility(View.GONE);
                if (layoutMortgageInfo != null) layoutMortgageInfo.setVisibility(View.VISIBLE);

                // Số tiền cần thanh toán kỳ tới
                if (tvMortgagePaymentAmount != null) {
                    tvMortgagePaymentAmount.setText(formatCurrency(actualData.paymentAmount));
                }

                // Ngày đến hạn và trạng thái quá hạn
                if (tvMortgageDueDate != null && actualData.nextPaymentDate != null) {
                    String formattedDate = formatDate(actualData.nextPaymentDate);
                    if (isOverdue(actualData.nextPaymentDate)) {
                        tvMortgageDueDate.setText("(Hạn: " + formattedDate + " - Quá hạn)");
                        tvMortgageDueDate.setTextColor(Color.RED);
                    } else {
                        tvMortgageDueDate.setText("(Hạn: " + formattedDate + ")");
                        tvMortgageDueDate.setTextColor(Color.parseColor("#6B7280")); // Màu xám mặc định
                    }
                }

                // Số dư nợ còn lại
                if (tvMortgageRemaining != null) {
                    tvMortgageRemaining.setText(formatCurrency(actualData.remainingBalance));
                }
                break;

            default:
                Log.w("DEBUG_HOME", "Loại tài khoản không xác định: " + accountType);
                break;
        }
    }
    private void toggleAccountNumber() {
        isAccountHidden = !isAccountHidden;
        updateAccountNumberDisplay();
    }
    private void updateAccountNumberDisplay() {
        if (tvAccountNumber == null) return;
        if (isAccountHidden) {
            String masked = "**** **** **** " + (realAccountNumber.length() > 4 ? realAccountNumber.substring(realAccountNumber.length() - 4) : "****");
            tvAccountNumber.setText(masked);
            imgToggleAccountNo.setImageResource(R.drawable.ic_eye_off);
        } else {
            tvAccountNumber.setText(formatAccountNumber(realAccountNumber));
            imgToggleAccountNo.setImageResource(R.drawable.ic_eye_on);
        }
    }
    private String formatAccountNumber(String accNum) {
        if (accNum == null) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < accNum.length(); i++) {
            if (i > 0 && i % 4 == 0) sb.append(" ");
            sb.append(accNum.charAt(i));
        }
        return sb.toString();
    }
    private String formatCurrency(Double amount) {
        if (amount == null) return "0 VND";
        return NumberFormat.getInstance(new Locale("vi", "VN")).format(amount) + " VND";
    }
    private String formatDate(String dateString) {
        Date date = parseDateString(dateString);
        if (date != null) {
            java.text.SimpleDateFormat outputFormat = new java.text.SimpleDateFormat("dd/MM/yyyy", new Locale("vi", "VN"));
            return outputFormat.format(date);
        }
        return dateString;
    }
    private boolean isOverdue(String dateString) {
        Date dueDate = parseDateString(dateString);
        if (dueDate == null) return false;
        Date now = new Date();
        return dueDate.before(now);
    }
    private Date parseDateString(String dateString) {
        if (dateString == null || dateString.isEmpty()) return null;

        String[] formats = {
                "EEE, dd MMM yyyy HH:mm:ss 'GMT'",
                "yyyy-MM-dd HH:mm:ss",
                "yyyy-MM-dd"
        };

        for (String format : formats) {
            try {
                java.text.SimpleDateFormat inputFormat = new java.text.SimpleDateFormat(format, Locale.ENGLISH);
                if (format.contains("GMT")) {
                    inputFormat.setTimeZone(java.util.TimeZone.getTimeZone("GMT"));
                }
                return inputFormat.parse(dateString);
            } catch (Exception e) {
                continue;
            }
        }
        return null;
    }

}