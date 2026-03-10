package com.keyly.model;

import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com.keyly.model.request.SucursalRequest;
import com.keyly.model.response.SucursalResponse;

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

    @UuidGenerator
    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

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

    public Sucursal(SucursalRequest request) {
        this.nom = request.nom();
        this.direccio = request.direccio();
        this.ciutat = request.ciutat();
        this.pais = request.pais();
        this.telefon = request.telefon();
        this.correu = request.correu();
    }

    public Sucursal(SucursalResponse response) {
        this.uuid = response.uuid();
        this.nom = response.nom();
        this.direccio = response.direccio();
        this.ciutat = response.ciutat();
        this.pais = response.pais();
        this.telefon = response.telefon();
        this.correu = response.correu();
    }

}
