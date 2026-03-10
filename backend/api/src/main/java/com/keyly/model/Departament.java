package com.keyly.model;

import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com.keyly.model.response.DepartamentResponse;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "Departaments")
public class Departament {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @UuidGenerator
    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @ManyToOne
    @JoinColumn(name = "sucursal_id", nullable = false)
    private Sucursal sucursal;

    @Column(name = "nom")
    private String nom;

    public Departament(Sucursal sucursal, String nom) {
        this.sucursal = sucursal;
        this.nom = nom;
    }

    public Departament(Sucursal sucursal, DepartamentResponse response) {
        this.sucursal = sucursal;
        this.nom = response.nom();
    }

}
