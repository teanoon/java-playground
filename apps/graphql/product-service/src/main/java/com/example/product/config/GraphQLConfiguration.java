package com.example.product.config;

import org.springframework.context.annotation.Configuration;

import com.example.graphql.entity.GraphQLEntityScan;
import com.example.graphql.service.EnableGraphQLService;
import com.example.product.model.Product;
import com.example.product.service.ProductService;

@Configuration
@GraphQLEntityScan(basePackageClasses = Product.class)
@EnableGraphQLService(basePackageClasses = ProductService.class)
public class GraphQLConfiguration {

}
