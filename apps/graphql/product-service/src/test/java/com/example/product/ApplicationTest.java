package com.example.product;

import graphql.schema.GraphQLSchema;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.example.graphql.example.GraphQLExampleService;
import com.example.product.config.TestCoreConfiguration;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestCoreConfiguration.class)
public class ApplicationTest {

    @Autowired
    private GraphQLExampleService service;

    @Autowired
    private GraphQLSchema schema;

    @Test
    public void test() {
        var result = service.query("mutation { create(arg0: { title: \"a-good-product\" }) { id, title } }", schema);
        System.out.println(result);

        result = service.query("query { findById(arg0: 1) { id, title } }", schema);
        System.out.println(result);
    }

}
