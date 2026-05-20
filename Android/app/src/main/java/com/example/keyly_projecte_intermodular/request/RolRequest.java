package com.example.keyly_projecte_intermodular.request;

import java.io.Serializable;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RolRequest implements Serializable {
    UUID sucursalUuid;
    String nom;
}
