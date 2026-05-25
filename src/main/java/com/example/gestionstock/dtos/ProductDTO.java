package com.example.gestionstock.dtos;

import com.example.gestionstock.entity.Category;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ProductDTO {
    private Long id;
    private String libelle;
    private Category category;
    private double prixUnitaire;
    private int quantiteEnStock;
    private String PathImage;
    private int seuilAlerte;
}
