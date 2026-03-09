package com.keyly.model;

import java.sql.Date;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import com.keyly.model.response.BagulResponse;

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
@Table(name = "Baguls")
public class Bagul {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @UuidGenerator
    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @ManyToOne
    @JoinColumn(name = "propietari_id", nullable = false)
    private Usuari propietari;

    @CreationTimestamp
    @Column(name = "data_creacio", updatable = false)
    private Date dataCreacio;

    public Bagul(Usuari propietari) {
        this.propietari = propietari;
    }

    public Bagul(BagulResponse response) {
        Sucursal s = new Sucursal(response.usuari().sucursal());

        this.propietari = new Usuari(
            new Sucursal(response.usuari().sucursal()),
            new Departament(s ,response.usuari().departament()),
            new Rol(s, response.usuari().rol()),
            response.usuari()
        );
    }

}
