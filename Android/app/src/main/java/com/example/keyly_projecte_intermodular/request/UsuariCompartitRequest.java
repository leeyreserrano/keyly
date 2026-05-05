package com.example.keyly_projecte_intermodular.request;

import com.example.keyly_projecte_intermodular.utils.Permisos;

import java.io.Serializable;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UsuariCompartitRequest implements Serializable {
    private UUID usuariUuid;
    private Permisos permis;
    private String encryptedDataKey;
}
