package com.example.zybanking.ui.transaction;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;

import com.example.zybanking.NavbarActivity;
import com.example.zybanking.R;
import com.example.zybanking.data.models.BasicResponse;
import com.example.zybanking.data.models.transaction.WithdrawRequest;
import com.example.zybanking.data.remote.ApiService;
import com.example.zybanking.ui.auth.OtpVerificationActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionActivity extends NavbarActivity {
    private ApiService apiService;
    private String sourceAccountId = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.basic_transfer);

        initNavbar();
        WithdrawRequest request = new WithdrawRequest("A001", 10.0000);

        apiService.withdrawCreate(request).enqueue(new Callback<BasicResponse>() {

            @Override
            public void onResponse(Call<BasicResponse> call,
                                   Response<BasicResponse> response) {

                if (response.isSuccessful()) {
                    String txId = response.body().transaction_id;

                    Intent i = new Intent(
                            TransactionActivity.this,
                            OtpVerificationActivity.class
                    );
                    i.putExtra("transaction_id", txId);
                    startActivity(i);
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                Toast.makeText(TransactionActivity.this,
                        t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}