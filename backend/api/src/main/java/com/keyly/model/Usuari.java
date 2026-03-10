package com.keyly.model;

import java.sql.Date;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import com.keyly.model.request.UsuariRequest;
import com.keyly.model.response.UsuariResponse;

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
@Table(name = "Usuaris")
public class Usuari {

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

    @ManyToOne
    @JoinColumn(name = "departament_id", nullable = false)
    private Departament departament;

    @ManyToOne
    @JoinColumn(name = "rol_id", nullable = false)
    private Rol rol;
    
    @Column(name = "nom")
    private String nom;

    @Column(name = "correu")
    private String correu;

    @Column(name = "imatge", nullable = true)
    private String imatge;

    @Column(name = "contrasenya_master")
    private String contrasenya;

    @CreationTimestamp
    @Column(name = "data_creacio", updatable = false)
    private Date dataCreacio;

    @CreationTimestamp
    @Column(name = "data_ultim_login", updatable = true)
    private Date dataUltimLogin;

    @Column(name = "pot_administrar")
    private Boolean potAdministrar;

    public Usuari(Sucursal sucursal, Departament departament, Rol rol, UsuariRequest request) {
        this.sucursal = sucursal;
        this.departament = departament;
        this.rol = rol;
        this.nom = request.nom();
        this.correu = request.correu();
        this.imatge = request.imatge();
        this.potAdministrar = request.potAdministrar();
    }

    public Usuari(Sucursal sucursal, Departament departament, Rol rol, UsuariResponse response) {
        this.sucursal = sucursal;
        this.departament = departament;
        this.rol = rol;
        this.nom = response.nom();
        this.correu = response.correu();
        this.imatge = response.imatge();
        this.dataCreacio = response.dataCreacio();
        this.dataUltimLogin = response.ultimLogin();
        this.potAdministrar = response.potAdministrar();
    }

}
