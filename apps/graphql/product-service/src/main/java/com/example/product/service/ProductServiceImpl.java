package com.example.product.service;

import org.springframework.stereotype.Service;

import com.example.graphql.annotation.GraphQLMutable;
import com.example.graphql.annotation.GraphQLQueryable;
import com.example.product.model.Product;
import com.example.product.repository.ProductRepository;

@Service
@GraphQLQueryable
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;

    public ProductServiceImpl(ProductRepository repository) {
        this.repository = repository;
    }

    @Override
    @GraphQLMutable
    public Product create(Product product) {
        return repository.save(product);
    }

    @Override
    public Product findById(Long id) {
        return repository.findById(id).orElse(null);
    }

}
