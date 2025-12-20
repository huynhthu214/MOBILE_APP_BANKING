package com.example.zybanking.ui.account;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.zybanking.NavbarActivity;
import com.example.zybanking.R;
import com.example.zybanking.data.models.BasicResponse;
import com.example.zybanking.data.models.UserResponse;
import com.example.zybanking.data.remote.ApiService;
import com.example.zybanking.data.remote.RetrofitClient;
import com.example.zybanking.ui.auth.ChangePasswordActivity;
import com.example.zybanking.ui.auth.LoginActivity;
import com.example.zybanking.ui.auth.ResetPasswordActivity;
import com.google.android.material.button.MaterialButton;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends NavbarActivity {
    private TextView tvName, tvEmail, tvPhone,tvEkycText;
    private LinearLayout btnChangePassword, btnEkycStatus, btnBiometric;
    private MaterialButton btnLogout;
    private ImageView btnEditEmail;
    private String currentUserId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        initViews();
        setupActions();
        loadUserProfile();
        initNavbar();
    }

    private void initViews() {
        tvName = findViewById(R.id.tv_profile_name);
        tvEmail = findViewById(R.id.tv_profile_email);
        tvPhone = findViewById(R.id.tv_profile_phone);
        tvEkycText = findViewById(R.id.tv_ekyc_text);

        btnChangePassword = findViewById(R.id.btn_change_password);
        btnEkycStatus = findViewById(R.id.btn_ekyc_status);
        btnBiometric = findViewById(R.id.btn_biometric_setting);
        btnLogout = findViewById(R.id.btn_logout);
        btnEditEmail = findViewById(R.id.btn_edit_email);
    }
    // Hàm riêng để xử lý logic hiển thị trạng thái EKYC
    private void updateEkycStatus(UserResponse.Ekyc ekyc) {
        if (ekyc == null) {
            // Chưa có hồ sơ EKYC
            tvEkycText.setText("Chưa xác thực");
            tvEkycText.setTextColor(Color.parseColor("#9CA3AF")); // Màu xám

            // Nếu chưa xác thực thì bấm vào cho đi làm EKYC (nếu có Activity đó)
            btnEkycStatus.setOnClickListener(v -> Toast.makeText(this, "Vui lòng thực hiện EKYC", Toast.LENGTH_SHORT).show());
            return;
        }

        String status = ekyc.getStatus(); // 'approved', 'pending', 'rejected'

        if ("approved".equalsIgnoreCase(status)) {
            tvEkycText.setText("Đã xác thực");
            tvEkycText.setTextColor(Color.parseColor("#4CAF50")); // Màu xanh lá
            btnEkycStatus.setOnClickListener(null); // Đã xong thì không cần bấm nữa hoặc bấm để xem chi tiết

        } else if ("pending".equalsIgnoreCase(status)) {
            tvEkycText.setText("Đang chờ duyệt");
            tvEkycText.setTextColor(Color.parseColor("#FF9800")); // Màu cam
            btnEkycStatus.setOnClickListener(v -> Toast.makeText(this, "Hồ sơ đang được xem xét", Toast.LENGTH_SHORT).show());

        } else if ("rejected".equalsIgnoreCase(status)) {
            tvEkycText.setText("Bị từ chối");
            tvEkycText.setTextColor(Color.parseColor("#F44336")); // Màu đỏ
            btnEkycStatus.setOnClickListener(v -> Toast.makeText(this, "Vui lòng thực hiện lại EKYC", Toast.LENGTH_SHORT).show());

        } else {
            // Trạng thái lạ
            tvEkycText.setText("Chưa xác thực");
            tvEkycText.setTextColor(Color.parseColor("#9CA3AF"));
        }
    }
    private void setupActions() {
        // 1. Chuyển sang màn hình Đổi mật khẩu
        btnChangePassword.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
        });

        // 2. Xử lý Đăng xuất
        btnLogout.setOnClickListener(v -> {
            SharedPreferences pref = getSharedPreferences("auth", MODE_PRIVATE);
            pref.edit().clear().apply();

            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // 3. Xử lý chỉnh sửa Email
        btnEditEmail.setOnClickListener(v -> showEditEmailDialog());

        btnEkycStatus.setOnClickListener(v -> Toast.makeText(this, "Tính năng đang phát triển", Toast.LENGTH_SHORT).show());
    }

    // --- HÀM TẠO GIAO DIỆN DIALOG ĐẸP BẰNG JAVA THUẦN ---
    private void showEditEmailDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cập nhật Email");

        // 1. Tạo Layout chứa (Container)
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);

        // Chuyển đổi dp sang pixel để set padding
        int paddingPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics());
        container.setPadding(paddingPx, paddingPx/2, paddingPx, paddingPx);

        // 2. Tạo dòng chữ hướng dẫn
        TextView tvLabel = new TextView(this);
        tvLabel.setText("Vui lòng nhập địa chỉ email mới:");
        tvLabel.setTextColor(Color.DKGRAY);
        tvLabel.setTextSize(14);

        LinearLayout.LayoutParams labelParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        labelParams.setMargins(0, 0, 0, 16); // Margin bottom
        tvLabel.setLayoutParams(labelParams);

        // 3. Tạo EditText nhập liệu
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        input.setHint("Ví dụ: example@gmail.com");
        input.setText(tvEmail.getText().toString()); // Điền sẵn email cũ
        input.setBackgroundResource(android.R.drawable.edit_text); // Dùng style mặc định cho rõ ràng
        input.setPadding(20, 20, 20, 20);

        // Thêm View vào Container
        container.addView(tvLabel);
        container.addView(input);

        // Set View cho Dialog
        builder.setView(container);

        // Nút Xác nhận
        builder.setPositiveButton("Lưu thay đổi", (dialog, which) -> {
            String newEmail = input.getText().toString().trim();
            if (!newEmail.isEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                updateUserEmail(newEmail);
            } else {
                Toast.makeText(ProfileActivity.this, "Email không đúng định dạng", Toast.LENGTH_SHORT).show();
            }
        });

        // Nút Hủy
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void updateUserEmail(String newEmail) {
        if (currentUserId == null || currentUserId.isEmpty()) {
            Toast.makeText(this, "Lỗi: Không tìm thấy User ID. Vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService api = RetrofitClient.getClient().create(ApiService.class);

        Map<String, Object> updateData = new HashMap<>();
        updateData.put("email", newEmail);
        updateData.put("EMAIL", newEmail);
        api.updateUser(currentUserId, updateData).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                if (response.isSuccessful()) {
                    tvEmail.setText(newEmail);
                    Toast.makeText(ProfileActivity.this, "Cập nhật email thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    // In ra mã lỗi để dễ debug (ví dụ 400, 500)
                    Toast.makeText(ProfileActivity.this, "Lỗi server: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Lỗi kết nối server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadUserProfile() {
        SharedPreferences pref = getSharedPreferences("auth", MODE_PRIVATE);
        String token = pref.getString("access_token", "");

        if (token.isEmpty()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        ApiService api = RetrofitClient.getClient().create(ApiService.class);
        api.getCurrentUser("Bearer " + token).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserResponse.Data data = response.body().getData();
                    UserResponse.User user = data.getUser();
                    UserResponse.Ekyc ekyc = data.getEkyc();

                    if (user != null) {
                        currentUserId = user.getUserId();

                        tvName.setText(user.getFullName());
                        tvEmail.setText(user.getEmail());
                        tvPhone.setText(user.getPhone() != null ? user.getPhone() : "Chưa cập nhật");
                        updateEkycStatus(ekyc);
                    }
                } else {
                    Toast.makeText(ProfileActivity.this, "Không thể tải thông tin", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Log.e("ProfileActivity", "Error: " + t.getMessage());
                Toast.makeText(ProfileActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }
}