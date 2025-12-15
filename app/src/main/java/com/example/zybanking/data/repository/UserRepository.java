package com.example.zybanking.data.repository;

import com.example.zybanking.data.remote.ApiService;
import com.example.zybanking.data.remote.RetrofitClient;

public class UserRepository {

    private ApiService api;

    public UserRepository() {
        api = RetrofitClient.getClient().create(ApiService.class);
    }

    public ApiService getApi() {
        return api;
    }
}
