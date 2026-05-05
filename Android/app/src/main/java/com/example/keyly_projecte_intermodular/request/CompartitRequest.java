package com.example.keyly_projecte_intermodular.request;

import com.example.keyly_projecte_intermodular.utils.TipusEntitat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CompartitRequest implements Serializable {
    private UUID entitatUuid;
    private TipusEntitat tipusEntitat;
    private ArrayList<UsuariCompartitRequest> usuaris;
}
