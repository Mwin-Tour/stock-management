package com.example.gestionstock.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String libelle;
    @ManyToOne
    private Category category;
    private double prixUnitaire;
    private String PathImage;
    private int quantiteEnStock;
    private int seuilAlerte;
}
