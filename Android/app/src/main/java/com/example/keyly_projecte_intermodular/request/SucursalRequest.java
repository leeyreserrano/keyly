package com.example.keyly_projecte_intermodular.request;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SucursalRequest implements Serializable {
    private String nom;
    private String direccio;
    private String ciutat;
    private String pais;
    private String telefon;
    private String correu;
}
