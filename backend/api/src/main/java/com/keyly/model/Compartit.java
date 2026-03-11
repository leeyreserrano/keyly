package com.keyly.model;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import com.keyly.model.enums.Permisos;
import com.keyly.model.enums.TipusEntitat;
import com.keyly.model.request.CompartitRequest;

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

    @UuidGenerator
    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @ManyToOne
    @JoinColumn(name = "usuari_id", nullable = false)
    private Usuari usuari;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipus_entitat", nullable = false)
    private TipusEntitat tipusEntitat;

    @Column(name = "entitat_uuid")
    private UUID entitatUuid;

    @Enumerated(EnumType.STRING)
    @Column(name = "permisos", nullable = false)
    private Permisos permisos;

    @CreationTimestamp
    @Column(name = "data_creacio", updatable = false)
    private LocalDateTime dataCreacio;

    public Compartit(Usuari usuari, CompartitRequest request) {
        this.usuari = usuari;
        this.tipusEntitat = request.tipusEntitat();
        this.entitatUuid = request.entitatUuid();
        this.permisos = request.permisos();
    }


}