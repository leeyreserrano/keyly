package com.keyly.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.keyly.model.request.ItemRequest;

import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(exclude = "carpetas")
@ToString(exclude = "carpetas")
@Entity
@Table(name = "Items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @UuidGenerator
    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @ManyToMany(mappedBy = "items")
    @JsonManagedReference
    private Set<Carpeta> carpetas = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "bagul_id", nullable = false)
    private Bagul bagul;

    @Column(name = "titol")
    private String titol;

    @Column(name = "nom_usuari")
    private String nomUsuari;

    @Column(name = "contrasenya")
    private String contrasenya;

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
    private LocalDateTime dataCreacio;

    @CreationTimestamp
    @Column(name = "data_editat", updatable = true)
    private LocalDateTime dataEditat;

    @CreationTimestamp
    @Column(name = "ultim_access", updatable = true)
    private LocalDateTime dataUltimAcces;

    public void addCarpeta(Carpeta carpeta) {
        carpetas.add(carpeta);
        carpeta.getItems().add(this);
    }

    public void removeCarpeta(Carpeta carpeta) {
        carpetas.remove(carpeta);
        carpeta.getItems().remove(this);
    }

    public Item(Bagul bagul, ItemRequest request) {
        this.bagul = bagul;
        this.titol = request.titol();
        this.nomUsuari = request.nomUsuari();
        this.contrasenya = request.contrasenya();
        this.url = request.url();
        this.notes = request.notes();
        this.favorit = request.favorit();
    }

}
