package dev.jlopez.monolitojwt.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.jlopez.monolitojwt.product.model.Product;
import java.util.List;


@Repository
public interface ProductRepository extends JpaRepository<Product, Integer>{

    List<Product> findByName(String name);

    boolean existsByName(String name);
}
