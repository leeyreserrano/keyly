package com.example.keyly_projecte_intermodular.request;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ItemRequest implements Serializable {
    private String titol;
    private String nomUsuari;
    private String contrasenya;
    private String iv;
    private String encryptedDataKey;
    private String url;
    private String notes;
    private boolean favorit;
}
