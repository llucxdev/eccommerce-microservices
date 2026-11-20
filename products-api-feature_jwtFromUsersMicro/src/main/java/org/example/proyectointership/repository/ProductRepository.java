package org.example.proyectointership.repository;

import org.example.proyectointership.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    boolean existsByProductTypeId(int id);
}
