package com.example.zybanking.ui.auth;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.zybanking.R;
import com.example.zybanking.data.models.BasicResponse;
import com.example.zybanking.data.models.auth.ChangePasswordRequest;
import com.example.zybanking.data.remote.ApiService;
import com.example.zybanking.data.remote.RetrofitClient;
import com.google.android.material.button.MaterialButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity {

    private ImageView btnBack;
    private EditText edtOldPass, edtNewPass, edtConfirmPass;
    private MaterialButton btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);

        initViews();

        btnBack.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> handleChangePassword());
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        edtOldPass = findViewById(R.id.edt_old_pass);
        edtNewPass = findViewById(R.id.edt_new_pass);
        edtConfirmPass = findViewById(R.id.edt_confirm_pass);
        btnSave = findViewById(R.id.btn_save_password);
    }

    private void handleChangePassword() {
        String oldPass = edtOldPass.getText().toString().trim();
        String newPass = edtNewPass.getText().toString().trim();
        String confirmPass = edtConfirmPass.getText().toString().trim();

        // 1. Validate
        if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPass.equals(confirmPass)) {
            Toast.makeText(this, "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
            return;
        }

        if (oldPass.equals(newPass)) {
            Toast.makeText(this, "Mật khẩu mới không được trùng cũ", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. Lấy Token từ bộ nhớ
        SharedPreferences pref = getSharedPreferences("auth", MODE_PRIVATE);
        String token = pref.getString("access_token", "");

        if (token.isEmpty()) {
            Toast.makeText(this, "Phiên đăng nhập hết hạn", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 3. Gọi API
        ApiService api = RetrofitClient.getClient().create(ApiService.class);
        ChangePasswordRequest request = new ChangePasswordRequest(oldPass, newPass);

        // --- QUAN TRỌNG: Truyền "Bearer " + token để sửa lỗi 401 ---
        api.changePassword("Bearer " + token, request).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                if (response.isSuccessful()) {
                    // --- THÀNH CÔNG ---
                    Toast.makeText(ChangePasswordActivity.this, "Cập nhật mật khẩu thành công!", Toast.LENGTH_SHORT).show();

                    // Đóng Activity này -> Tự động quay về ProfileActivity
                    finish();
                } else {
                    // Xử lý lỗi (Ví dụ: Sai mật khẩu cũ)
                    if (response.code() == 401) {
                        Toast.makeText(ChangePasswordActivity.this, "Mật khẩu cũ không chính xác", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ChangePasswordActivity.this, "Lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                Toast.makeText(ChangePasswordActivity.this, "Lỗi kết nối mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }
}