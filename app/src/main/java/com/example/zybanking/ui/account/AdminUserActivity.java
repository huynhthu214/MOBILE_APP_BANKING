package com.example.zybanking.ui.account;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zybanking.HeaderAdmin;
import com.example.zybanking.R;
import com.example.zybanking.data.adapter.AdminUserAdapter;
import com.example.zybanking.data.models.auth.UserListResponse;
import com.example.zybanking.data.models.auth.User;
import com.example.zybanking.data.remote.ApiService;
import com.example.zybanking.data.remote.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminUserActivity extends HeaderAdmin {
    private ImageView addUser;
    private RecyclerView rvUsers;
    private EditText edtSearch;
    private AdminUserAdapter adapter;
    private List<User> userList = new ArrayList<>();
    private ApiService apiService;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_user);
        initHeader();

        apiService = RetrofitClient.getClient().create(ApiService.class);
        loadToken();

        addUser = findViewById(R.id.add_user);
        rvUsers = findViewById(R.id.rvTransactions); // Note: Your XML ID is rvTransactions, maybe rename to rvUsers
        edtSearch = findViewById(R.id.edtSearch);

        addUser.setOnClickListener(v -> startActivity(new Intent(this, CreateAccountActivity.class)));

        // Setup RecyclerView
        rvUsers.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminUserAdapter(this, userList, new AdminUserAdapter.OnUserClickListener() {
            @Override
            public void onUserClick(User user) {
                if (user != null && user.getUserId() != null) {
                    Intent intent = new Intent(AdminUserActivity.this, AdminDetailUserActivity.class);
                    intent.putExtra("USER_ID", user.getUserId());
                    startActivity(intent);
                } else {
                    Toast.makeText(AdminUserActivity.this, "Dữ liệu User không hợp lệ", Toast.LENGTH_SHORT).show();
                }
            }
        });
        rvUsers.setAdapter(adapter);
        // Load Data
        loadUsers("");

        // Search Logic
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loadUsers(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadToken() {
        SharedPreferences pref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String savedToken = pref.getString("auth_token", "");
        token = savedToken.startsWith("Bearer ") ? savedToken : "Bearer " + savedToken;
    }

    private void loadUsers(String query) {
        apiService.getAdminUsers(token, query).enqueue(new Callback<UserListResponse>() {
            @Override
            public void onResponse(Call<UserListResponse> call, Response<UserListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    userList.clear();
                    userList.addAll(response.body().getData());
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onFailure(Call<UserListResponse> call, Throwable t) {
                Toast.makeText(AdminUserActivity.this, "Lỗi tải danh sách", Toast.LENGTH_SHORT).show();
            }
        });
    }
}