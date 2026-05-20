package com.example.keyly_projecte_intermodular.request;

import com.example.keyly_projecte_intermodular.utils.RolIntern;

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
    private String kdfSalt;
    private String publicKey;
    private String encryptedPrivateKey;
    private RolIntern rolIntern;
    private boolean potAdministrar;
}
