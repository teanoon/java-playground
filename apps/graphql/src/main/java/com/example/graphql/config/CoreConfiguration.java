package com.example.graphql.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.example.graphql.model.Product;
import com.example.graphql.repository.ProductRepository;

@Configuration
@EntityScan(basePackageClasses = Product.class)
@EnableJpaRepositories(basePackageClasses = ProductRepository.class)
public class CoreConfiguration {

    @Bean
    public Product simpleEmptyTestProduct() {
        return new Product();
    }

}
