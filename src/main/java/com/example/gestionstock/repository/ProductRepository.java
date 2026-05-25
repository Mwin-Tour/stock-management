package com.example.gestionstock.repository;

import com.example.gestionstock.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // count all products
    public long count();
}
