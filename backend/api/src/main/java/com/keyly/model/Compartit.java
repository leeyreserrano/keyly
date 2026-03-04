package com.keyly.model;

import java.sql.Date;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "Compartits")
public class Compartit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuari_id", nullable = false)
    private Usuari usuari;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipus_entitat", nullable = false)
    private TipusEntitat tipusEntitat;

    @Column(name = "entitat_id")
    private Long entitatId;

    @Enumerated(EnumType.STRING)
    @Column(name = "permisos", nullable = false)
    private Permisos permisos;

    @CreationTimestamp
    @Column(name = "data_creacio", updatable = false)
    private Date dataCreacio;

}

enum TipusEntitat {
    CARPETA,
    ITEM
}

enum Permisos {
    LECTURA,
    ESCRIPTURA,
    ADMINISTRADOR
}