package com.example.product.controller;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import com.example.product.model.Product;
import com.example.product.service.ProductService;

@Controller
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @QueryMapping
    public Product findById(@Argument Long id) {
        return productService.findById(id);
    }

    @MutationMapping
    public Product create(@Argument Product product) {
        return productService.create(product);
    }

}
