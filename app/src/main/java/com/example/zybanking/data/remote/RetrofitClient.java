package com.example.zybanking.data.remote;

import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit = null;
    private static final String BASE_URL = "http://10.0.2.2:5000/api/v1/";

    public static Retrofit getClient() {
        if (retrofit == null) {
            // --- CẤU HÌNH TIMEOUT ---
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS) // Tăng lên 60s
                    .readTimeout(60, TimeUnit.SECONDS)    // Tăng lên 60s (Quan trọng để tải ảnh)
                    .writeTimeout(60, TimeUnit.SECONDS)   // Tăng lên 60s (Quan trọng để upload ảnh)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client) // <-- Đừng quên dòng này
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}