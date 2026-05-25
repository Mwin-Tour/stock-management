package com.example.gestionstock.mapper;

import com.example.gestionstock.dtos.CommadeDTO;
import com.example.gestionstock.dtos.ProductDTO;
import com.example.gestionstock.entity.Commande;
import com.example.gestionstock.entity.Product;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CommandeMapper {
    // version SANS mapping des produits
    public static Commande toCommandeSansProduits(CommadeDTO commandeDto) {
        Commande commande = new Commande();
        commande.setId(commandeDto.getId());
        commande.setClientInfo(commandeDto.getClientInfo());
        commande.setPrixTotal(commandeDto.getPrixTotal());
        commande.setProductIds(commandeDto.getProductIds());
        commande.setQuantities(commandeDto.getQuantities());
        commande.setCreatAt(commandeDto.getCreatAt());
        return commande;
    }

    public static CommadeDTO toCommadeDTO(Commande commande) {
        CommadeDTO commandeDto = new CommadeDTO();
        List<ProductDTO> productDTOS = commande.getProducts().stream()
                .map(ProductMapper::toProductDTO)
                .toList();

        commandeDto.setId(commande.getId());
        commandeDto.setClientInfo(commande.getClientInfo());
        commandeDto.setProducts(productDTOS);
        commandeDto.setPrixTotal(commande.getPrixTotal());
        commandeDto.setProductIds(commande.getProductIds());
        commandeDto.setQuantities(commande.getQuantities());
        commandeDto.setCreatAt(commande.getCreatAt());
        return commandeDto;
    }
}