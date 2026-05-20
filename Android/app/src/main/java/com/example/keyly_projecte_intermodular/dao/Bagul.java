package com.example.keyly_projecte_intermodular.dao;

import java.io.Serializable;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Bagul implements Serializable {
    private UUID uuid;
    private Usuari usuari;

}
