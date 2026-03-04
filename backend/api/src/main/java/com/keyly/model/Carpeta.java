package com.keyly.model;

import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;

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

    @CreationTimestamp
    @Column(name = "data_creacio")
    private Date dataCreacio;

    public void addItem(Item item) {
        items.add(item);
        item.getCarpetas().add(this);
    }

    public void removeItem(Item item) {
        items.remove(item);
        item.getCarpetas().remove(this);
    }

}
