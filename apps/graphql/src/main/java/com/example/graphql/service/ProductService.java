package com.example.graphql.service;

import com.example.graphql.model.Product;

public interface ProductService {

    Product create(Product product);

    Product findById(Long id);

}
