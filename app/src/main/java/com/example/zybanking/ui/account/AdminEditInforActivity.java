package com.example.zybanking.ui.account;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zybanking.HeaderAdmin;
import com.example.zybanking.R;
import com.example.zybanking.data.models.auth.User;
import com.example.zybanking.data.models.auth.UserResponse;
import com.example.zybanking.data.remote.ApiService;
import com.example.zybanking.data.remote.RetrofitClient;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminEditInforActivity extends HeaderAdmin {

    private ImageView btnBack;
    private TextView btnDelete;
    private Button btnSave;

    // Các trường nhập liệu
    private TextInputEditText etFullName, etIdentity, etDob, etPhone, etEmail, etAddress;
    private SwitchMaterial switchStatus;

    private ApiService apiService;
    private String token;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_edit_infor_user); // Đảm bảo đúng tên file XML của bạn

        // 1. Lấy Token và User ID từ Intent
        SharedPreferences pref = getSharedPreferences("auth", Context.MODE_PRIVATE);
        token = "Bearer " + pref.getString("access_token", "");
        userId = getIntent().getStringExtra("USER_ID");

        apiService = RetrofitClient.getClient().create(ApiService.class);

        initViews();

        if (userId != null && !userId.isEmpty()) {
            loadUserData();
        } else {
            Toast.makeText(this, "Lỗi: Không tìm thấy User ID", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back_edit);
        btnDelete = findViewById(R.id.btn_delete_customer);
        btnSave = findViewById(R.id.btn_save_changes);

        // Ánh xạ View từ XML
        etFullName = findViewById(R.id.et_edit_fullname);
        etIdentity = findViewById(R.id.et_edit_identity);
        etDob = findViewById(R.id.et_edit_dob);
        etPhone = findViewById(R.id.et_edit_phone);
        etEmail = findViewById(R.id.et_edit_email);
        etAddress = findViewById(R.id.et_edit_address);
        switchStatus = findViewById(R.id.switch_account_status);

        // Sự kiện
        btnBack.setOnClickListener(v -> finish());

        btnSave.setOnClickListener(v -> saveChanges());

        btnDelete.setOnClickListener(v -> {
            Toast.makeText(this, "Chức năng xóa đang phát triển", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadUserData() {
        apiService.getUserDetail(token, userId).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    User user = response.body().getData().getUser();

                    // Bind dữ liệu lên UI
                    etFullName.setText(user.getFullName());
                    etEmail.setText(user.getEmail());
                    etPhone.setText(user.getPhone());
                    switchStatus.setChecked(user.isActive());

                    // === XỬ LÝ CÁC TRƯỜNG CHƯA CÓ TRONG DB ===
                    // Bảng USER hiện tại chỉ có: USER_ID, FULL_NAME, EMAIL, PHONE, ROLE, IS_ACTIVE
                    // Các trường CMND, Ngày sinh, Địa chỉ chưa có cột trong DB -> Disable

                    etIdentity.setText("N/A (Chưa có trong DB)");
                    etIdentity.setEnabled(false);

                    etDob.setText("N/A (Chưa có trong DB)");
                    etDob.setEnabled(false);

                    etAddress.setText("N/A (Chưa có trong DB)");
                    etAddress.setEnabled(false);
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(AdminEditInforActivity.this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveChanges() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        boolean isActive = switchStatus.isChecked();

        if (fullName.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Tên và Email không được để trống", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo Map body để gửi lên API Update
        // Key phải khớp với các cột mà backend cho phép update (xem user_model.py: update_user)
        Map<String, Object> body = new HashMap<>();
        body.put("FULL_NAME", fullName);
        body.put("EMAIL", email);
        body.put("PHONE", phone);
        body.put("IS_ACTIVE", isActive ? 1 : 0);

        apiService.updateUser(token, userId, body).enqueue(new Callback<Map<String, Object>>() { // Chú ý kiểu trả về của updateUser
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminEditInforActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    // Đóng activity để quay lại trang chi tiết (nó sẽ reload lại dữ liệu mới)
                    finish();
                } else {
                    Toast.makeText(AdminEditInforActivity.this, "Lỗi cập nhật: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(AdminEditInforActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }
}