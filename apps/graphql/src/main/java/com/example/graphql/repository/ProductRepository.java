package com.example.graphql.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.graphql.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

}
