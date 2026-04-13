package com.example.keyly_projecte_intermodular.dao;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Bagul {
    private UUID uuid;
    private Usuari usuari;

}
