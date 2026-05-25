package com.example.gestionstock.mapper;

import com.example.gestionstock.dtos.ProductDTO;
import com.example.gestionstock.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public static Product toProduct(ProductDTO productDto) {
        Product product = new Product();
        product.setId(productDto.getId());
        product.setLibelle(productDto.getLibelle());
        product.setCategory(productDto.getCategory());
        product.setPrixUnitaire(productDto.getPrixUnitaire());
        product.setQuantiteEnStock(productDto.getQuantiteEnStock());
        product.setSeuilAlerte(productDto.getSeuilAlerte());
        product.setPathImage(productDto.getPathImage());
        return product;
    }

    public static ProductDTO toProductDTO(Product product) {
        ProductDTO productDto = new ProductDTO();
        productDto.setId(product.getId());
        productDto.setLibelle(product.getLibelle());
        productDto.setCategory(product.getCategory());
        productDto.setPrixUnitaire(product.getPrixUnitaire());
        productDto.setQuantiteEnStock(product.getQuantiteEnStock());
        productDto.setSeuilAlerte(product.getSeuilAlerte());
        productDto.setPathImage(product.getPathImage());
        return productDto;
    }
}
