package com.virtualfittingroom.data.api;

import androidx.annotation.Nullable;

import retrofit2.Retrofit;

public class BaseApi {
    protected Retrofit retrofit;

    public BaseApi(String base_url, @Nullable String token) {
        if(token==null) token = "";

        retrofit = new RetrofitBuilder().build(base_url, token);
    }
}
