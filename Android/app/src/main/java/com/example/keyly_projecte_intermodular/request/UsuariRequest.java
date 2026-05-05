package com.example.keyly_projecte_intermodular.request;

import java.io.Serializable;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UsuariRequest implements Serializable {
    private UUID sucursalUuid;
    private UUID departamentUuid;
    private UUID rolUuid;
    private String nom;
    private String correu;
    private String contrasenya;
    private boolean potAdministrar;
}
