package com.example.keyly_projecte_intermodular.rest_api;

import com.example.keyly_projecte_intermodular.dao.Usuari;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UsuariResponse {

    @SerializedName("usuari")
    private Usuari usuari;

}
