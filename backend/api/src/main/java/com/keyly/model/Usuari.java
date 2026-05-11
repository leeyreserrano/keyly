package com.keyly.model;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import com.keyly.model.enums.RolIntern;
import com.keyly.model.request.UsuariRequest;
import com.keyly.model.response.UsuariResponse;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
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
    @Column(nullable = false, unique = true, updatable = false, columnDefinition = "BINARY(16)")
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

    @Enumerated(EnumType.STRING)
    @Column(name = "rol_intern")
    private RolIntern rolIntern;

    @PrePersist
    public void prePresist() {
        if (rolIntern == null) {
            rolIntern = RolIntern.USUARI;
        }
    }
    
    @Column(name = "nom")
    private String nom;

    @Email
    @Column(name = "correu")
    private String correu;

    @Column(name = "imatge", nullable = true)
    private String imatge;

    @Column(name = "contrasenya_master")
    private String contrasenya;

    @Column(name = "kdf_salt")
    private byte[] kdfSalt;

    @Column(name= "public_key")
    private String publicKey;

    @Column(name = "encrypted_private_key")
    private String encryptedPrivateKey;

    @CreationTimestamp
    @Column(name = "data_creacio", updatable = false)
    private LocalDateTime dataCreacio;

    @Column(name = "data_ultim_login", updatable = true)
    private LocalDateTime dataUltimLogin;

    @Column(name = "pot_administrar")
    private Boolean potAdministrar;

    public Usuari(Sucursal sucursal, Departament departament, Rol rol, UsuariRequest request) {
        this.sucursal = sucursal;
        this.departament = departament;
        this.rol = rol;
        this.nom = request.nom();
        this.correu = request.correu();
        this.kdfSalt = Base64.getDecoder().decode(request.kdfSalt());
        this.publicKey = request.publicKey();
        this.encryptedPrivateKey = request.encryptedPrivateKey();
        this.potAdministrar = request.potAdministrar();
        this.rolIntern = request.rolIntern();
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
        this.rolIntern = response.rolIntern();
    }

}
