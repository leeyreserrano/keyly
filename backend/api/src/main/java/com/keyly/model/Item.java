package com.keyly.model;

import java.sql.Date;

import org.hibernate.annotations.CreationTimestamp;

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
@Table(name = "Items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @ManyToOne
    @JoinColumn(name = "bagul_id", nullable = false)
    private Bagul bagul;

    @Column(name = "titol")
    private String titol;

    @Column(name = "nom_usuari")
    private String nomUsuari;

    @Column(name = "contrasenya")
    private byte[] contrasenya;

    @Column(name = "iv")
    private byte[] iv;

    @Column(name = "url")
    private String url;

    @Column(name = "notes")
    private String notes;

    @Column(name = "favorit")
    private Boolean favorit;

    @CreationTimestamp
    @Column(name = "data_creacio", updatable = false)
    private Date dataCreacio;

    @CreationTimestamp
    @Column(name = "data_editat", updatable = true)
    private Date dataEditat;

    @CreationTimestamp
    @Column(name = "ultim_access", updatable = true)
    private Date dataUltimAcces;

}
