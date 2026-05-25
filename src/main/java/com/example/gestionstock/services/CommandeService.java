package com.example.gestionstock.services;

import com.example.gestionstock.dtos.CommadeDTO;
import com.example.gestionstock.entity.Commande;
import com.example.gestionstock.entity.Product;

import javax.swing.*;
import java.util.LinkedHashMap;
import java.util.List;

public interface CommandeService {
    // interface pour les service de gestions des commandes
    public CommadeDTO addCommande(CommadeDTO commande, List<Long> productIds,LinkedHashMap<Product,Integer> map);
    public CommadeDTO getCommandeById(Long id);
    public List<CommadeDTO> getAllCommande();
    public CommadeDTO updateCommade(CommadeDTO commande);
    public void deleteCommade(Long id);
    public Double getTotalPriceOfAllCommandes();
    public Double getTotalPriceOfCommande(Long id);
    byte[] genererFacturePourCommande(Long id,LinkedHashMap<Product,Integer> map);
    public LinkedHashMap<Product,Integer> matchProductsToCommande(List<Long> productIds, List<Integer> quantitie);
    public Double calculCommandePriceWithProduct(LinkedHashMap<Product,Integer> list);
    public Boolean verifierQuantiteEnStock(LinkedHashMap<Product,Integer> commande);
}
