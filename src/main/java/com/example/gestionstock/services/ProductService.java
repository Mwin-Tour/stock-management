package com.example.gestionstock.services;

import com.example.gestionstock.dtos.ProductDTO;
import com.example.gestionstock.entity.Product;

import java.util.List;

public interface ProductService {
    // Interface pour les services de gestion des produits
    public ProductDTO getProductById(Long productId);
    public List<ProductDTO> getAllProducts();
    public ProductDTO addProduct(ProductDTO product);
    public ProductDTO updateProduct(ProductDTO product);
    public void deleteProduct(Long productId);
    public List<ProductDTO> getProductsByIds(List<Long> productIds);
    public long countAllProducts();
    public long nombreProduitParEnStockFaible();
}
