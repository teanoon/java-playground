package com.example.product.service;

import com.example.product.model.Product;

public interface ProductService {

    Product create(Product product);

    Product findById(Long id);

}
