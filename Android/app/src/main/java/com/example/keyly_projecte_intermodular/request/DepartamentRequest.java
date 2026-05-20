package com.example.keyly_projecte_intermodular.request;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DepartamentRequest {
    private UUID sucursalUuid;
    private String nom;
}
