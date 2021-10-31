package com.example.product.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.example.product.service.ProductService;

@Configuration
@EnableAutoConfiguration
@Import({CoreConfiguration.class, GraphQLConfiguration.class})
@ComponentScan(basePackageClasses = ProductService.class)
public class TestCoreConfiguration {

}
