package com.keyly.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "Sucursals")
public class Sucursal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column(name = "nom")
    private String nom;

    @Column(name = "direccio")
    private String direccio;

    @Column(name = "ciutat")
    private String ciutat;

    @Column(name = "pais")
    private String pais;

    @Column(name = "telefon")
    private String telefon;

    @Column(name = "correu")
    private String correu;

}
