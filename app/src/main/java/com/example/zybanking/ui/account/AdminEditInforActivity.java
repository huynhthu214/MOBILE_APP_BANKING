package com.example.zybanking.ui.account;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zybanking.HeaderAdmin;
import com.example.zybanking.R;
// Import đúng class User
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

    private TextInputEditText etFullName, etIdentity, etDob, etPhone, etEmail, etAddress;
    private SwitchMaterial switchStatus;

    private ApiService apiService;
    private String token;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_edit_infor_user); // Đảm bảo tên layout đúng

        // 1. Lấy Token & ID
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

        etFullName = findViewById(R.id.et_edit_fullname);
        etIdentity = findViewById(R.id.et_edit_identity);
        etDob = findViewById(R.id.et_edit_dob);
        etPhone = findViewById(R.id.et_edit_phone);
        etEmail = findViewById(R.id.et_edit_email);
        etAddress = findViewById(R.id.et_edit_address);
        switchStatus = findViewById(R.id.switch_account_status);

        btnBack.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> saveChanges());
        btnDelete.setOnClickListener(v -> Toast.makeText(this, "Chức năng xóa đang phát triển", Toast.LENGTH_SHORT).show());
    }

    private void loadUserData() {
        // Log để kiểm tra ID gửi đi
        Log.d("AdminEdit", "Loading user ID: " + userId);

        apiService.getUserDetail(token, userId).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                // Log response code
                Log.d("AdminEdit", "Response Code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    UserResponse.Data data = response.body().getData();

                    if (data != null && data.getUser() != null) {
                        User user = data.getUser(); // Dùng class User độc lập

                        // Bind dữ liệu
                        etFullName.setText(user.getFullName());
                        etEmail.setText(user.getEmail());
                        etPhone.setText(user.getPhone());
                        switchStatus.setChecked(user.isActive());

                        // Các trường chưa có trong DB -> Set mặc định
                        etIdentity.setText("N/A");
                        etIdentity.setEnabled(false);
                        etDob.setText("N/A");
                        etDob.setEnabled(false);
                        etAddress.setText("N/A");
                        etAddress.setEnabled(false);
                    } else {
                        Log.e("AdminEdit", "Data or User object is NULL");
                        Toast.makeText(AdminEditInforActivity.this, "Dữ liệu người dùng trống", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("AdminEdit", "Error Body: " + response.message());
                    Toast.makeText(AdminEditInforActivity.this, "Lỗi tải dữ liệu: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Log.e("AdminEdit", "Failure: " + t.getMessage());
                Toast.makeText(AdminEditInforActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
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

        Map<String, Object> body = new HashMap<>();
        body.put("FULL_NAME", fullName);
        body.put("EMAIL", email);
        body.put("PHONE", phone);
        body.put("IS_ACTIVE", isActive ? 1 : 0);

        apiService.updateUser(token, userId, body).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminEditInforActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
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