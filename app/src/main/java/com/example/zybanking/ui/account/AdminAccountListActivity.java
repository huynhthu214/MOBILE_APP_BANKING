package com.example.zybanking.ui.account;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.zybanking.HeaderAdmin;
import com.example.zybanking.R;
import com.example.zybanking.data.adapter.AdminAccountAdapter;
import com.example.zybanking.data.models.account.Account;
import com.example.zybanking.data.models.account.AccountListResponse;
import com.example.zybanking.data.remote.ApiService;
import com.example.zybanking.data.remote.RetrofitClient;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminAccountListActivity extends HeaderAdmin {
    private RecyclerView rvAccounts;
    private AdminAccountAdapter adapter;
    private List<Account> accountList = new ArrayList<>();
    private EditText edtSearch;
    private ApiService apiService;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_account_list); // Layout chứa rvAccountsList

        initHeader();
        initViews();
        setupApiService();

        loadAccounts(""); // Load mặc định
    }

    private void initViews() {
        edtSearch = findViewById(R.id.edtSearchAccount);
        rvAccounts = findViewById(R.id.rvAccountsList);
        if (edtSearch != null) {
            edtSearch.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                    loadAccounts(s.toString().trim());
                }
                @Override public void afterTextChanged(Editable s) {}
            });
        }
        rvAccounts.setLayoutManager(new LinearLayoutManager(this));
        if (accountList == null) accountList = new ArrayList<>();
        adapter = new AdminAccountAdapter(this, accountList);
        rvAccounts.setAdapter(adapter);

        // Xử lý tìm kiếm khi nhập liệu
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loadAccounts(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupApiService() {
        apiService = RetrofitClient.getClient().create(ApiService.class);
        SharedPreferences pref = getSharedPreferences("auth", Context.MODE_PRIVATE);
        token = "Bearer " + pref.getString("access_token", "");
    }

    private void loadAccounts(String query) {
        if (apiService == null || token == null) return; // Tránh crash nếu chưa init xong

        apiService.getAdminAccounts(token, query).enqueue(new Callback<AccountListResponse>() {
            @Override
            public void onResponse(Call<AccountListResponse> call, Response<AccountListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    accountList.clear();
                    if (response.body().getData() != null) {
                        accountList.addAll(response.body().getData());
                    }
                    // Chỉ notify khi adapter đã được gán
                    if (adapter != null) adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<AccountListResponse> call, Throwable t) {
                Toast.makeText(AdminAccountListActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}