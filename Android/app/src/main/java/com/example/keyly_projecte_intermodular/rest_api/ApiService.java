package com.example.keyly_projecte_intermodular.rest_api;

import com.example.keyly_projecte_intermodular.dao.LoginDto;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    // Login amb usuari i contrasenya
    @POST("/api/auth/login")
    Call<ResponseBody> getToken(@Body LoginDto loginDto);
}