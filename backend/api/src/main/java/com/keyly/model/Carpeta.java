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
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.keyly.model.request.CarpetaRequest;
import com.keyly.model.response.CarpetaResponse;

import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(exclude = "items")
@ToString(exclude = "items")
@Entity
@Table(name = "Carpetes")
public class Carpeta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @UuidGenerator
    @Column(nullable = false, unique = true, updatable = false, columnDefinition = "BINARY(16)")
    private UUID uuid;

    @ManyToMany
    @JoinTable(
        name = "Carpetes_Items", 
        joinColumns = @JoinColumn(name = "carpeta_id"), 
        inverseJoinColumns = @JoinColumn(name = "item_id")
    )
    @JsonBackReference
    private Set<Item> items = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "bagul_id", nullable = false)
    private Bagul bagul;

    @Column(name = "nom")
    private String nom;

    @Column(name = "favorit")
    private Boolean favorit;

    @CreationTimestamp
    @Column(name = "data_creacio", updatable = false)
    private LocalDateTime dataCreacio;

    @Column(name = "data_editat", updatable = true)
    private LocalDateTime dataEditat;

    @Column(name = "ultim_access", updatable = true)
    private LocalDateTime ultimAccess;

    @Column(name = "comptador_access")
    private Long comptadorAccess = 0L;

    public void addItem(Item item) {
        items.add(item);
        item.getCarpetas().add(this);
    }

    public void removeItem(Item item) {
        items.remove(item);
        item.getCarpetas().remove(this);
    }

    public Carpeta(Bagul bagul, CarpetaRequest request) {
        this.bagul = bagul;
        this.nom = request.nom();
    }

    public Carpeta(Bagul bagul, CarpetaResponse response) {
        this.bagul = bagul;
        this.nom = response.nom();
        this.dataCreacio = response.dataCreacio();
    }

}
