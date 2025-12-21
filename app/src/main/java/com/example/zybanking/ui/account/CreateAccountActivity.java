package com.example.zybanking.ui.account;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.zybanking.R;
import com.example.zybanking.data.models.BasicResponse;
import com.example.zybanking.data.models.auth.CreateUserRequest;
import com.example.zybanking.data.remote.ApiService;
import com.example.zybanking.data.remote.RetrofitClient;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateAccountActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextInputEditText etFullname, etEmail, etPhone, etPassword; // Đã bỏ etIdentity
    private Button btnCreateUser;
    private ApiService apiService;
    private String token;
    private ProgressDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_create_customer);

        apiService = RetrofitClient.getClient().create(ApiService.class);
        loadToken();
        initViews();

        btnBack.setOnClickListener(v -> finish());
        btnCreateUser.setOnClickListener(v -> handleCreateUser());
    }

    private void loadToken() {
        SharedPreferences pref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String savedToken = pref.getString("auth_token", "");
        token = savedToken.startsWith("Bearer ") ? savedToken : "Bearer " + savedToken;
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back_admin);
        etFullname = findViewById(R.id.et_fullname);
        etEmail = findViewById(R.id.et_email);
        etPhone = findViewById(R.id.et_phone);
        etPassword = findViewById(R.id.et_password);
        btnCreateUser = findViewById(R.id.btn_create_user);

        loadingDialog = new ProgressDialog(this);
        loadingDialog.setMessage("Đang tạo tài khoản...");
        loadingDialog.setCancelable(false);
    }

    private void handleCreateUser() {
        // 1. Reset lỗi cũ
        etFullname.setError(null);
        etEmail.setError(null);
        etPhone.setError(null);
        etPassword.setError(null);

        // 2. Lấy dữ liệu
        String fullName = etFullname.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // 3. Kiểm tra Ràng buộc (Validation)
        boolean hasError = false;

        if (TextUtils.isEmpty(fullName)) {
            etFullname.setError("Vui lòng nhập họ tên");
            etFullname.requestFocus();
            hasError = true;
        }

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Vui lòng nhập email");
            if (!hasError) etEmail.requestFocus();
            hasError = true;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Email không hợp lệ");
            if (!hasError) etEmail.requestFocus();
            hasError = true;
        }

        if (TextUtils.isEmpty(phone)) {
            etPhone.setError("Vui lòng nhập số điện thoại");
            if (!hasError) etPhone.requestFocus();
            hasError = true;
        } else if (phone.length() < 9 || phone.length() > 11) {
            etPhone.setError("Số điện thoại phải từ 9-11 số");
            if (!hasError) etPhone.requestFocus();
            hasError = true;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Vui lòng nhập mật khẩu");
            if (!hasError) etPassword.requestFocus();
            hasError = true;
        } else if (password.length() < 6) {
            etPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
            if (!hasError) etPassword.requestFocus();
            hasError = true;
        }

        if (hasError) return; // Dừng lại nếu có lỗi nhập liệu

        // 4. Gọi API
        loadingDialog.show();
        CreateUserRequest request = new CreateUserRequest(fullName, email, phone, password);

        apiService.createCustomer(token, request).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                loadingDialog.dismiss();

                if (response.isSuccessful() && response.body() != null) {
                    // Thành công
                    Toast.makeText(CreateAccountActivity.this, "Tạo tài khoản thành công!", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    // Thất bại (Lỗi từ Backend trả về: 400, 409...)
                    String errorMessage = "Lỗi tạo tài khoản";
                    try {
                        // Đọc lỗi chi tiết từ JSON lỗi
                        if (response.errorBody() != null) {
                            String errorJson = response.errorBody().string();
                            // Parse JSON lỗi để lấy message. Ví dụ: {"message": "Email already exists"}
                            Map<String, Object> errorMap = new Gson().fromJson(errorJson, new TypeToken<Map<String, Object>>(){}.getType());
                            if (errorMap.containsKey("message")) {
                                errorMessage = String.valueOf(errorMap.get("message"));
                            } else if (errorMap.containsKey("error")) {
                                errorMessage = String.valueOf(errorMap.get("error"));
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // Hiển thị thông báo lỗi cụ thể cho người dùng
                    if (errorMessage.toLowerCase().contains("email")) {
                        etEmail.setError(errorMessage);
                        etEmail.requestFocus();
                    } else if (errorMessage.toLowerCase().contains("phone") || errorMessage.toLowerCase().contains("số điện thoại")) {
                        etPhone.setError(errorMessage);
                        etPhone.requestFocus();
                    } else {
                        Toast.makeText(CreateAccountActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                loadingDialog.dismiss();
                Toast.makeText(CreateAccountActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}