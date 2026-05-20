package com.example.keyly_projecte_intermodular.rest_api;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PrivateKeyResponse {
    @SerializedName("encryptedPrivateKey")
    private String privateKey;
}
