package com.example.graphql.service;

import java.util.List;

public class GraphQLServiceCollection {

    private final List<Class<?>> klasses;

    GraphQLServiceCollection(List<Class<?>> klasses) {
        this.klasses = klasses;
    }

    public List<Class<?>> getKlasses() {
        return klasses;
    }

}
