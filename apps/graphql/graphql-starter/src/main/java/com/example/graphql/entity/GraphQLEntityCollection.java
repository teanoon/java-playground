package com.example.graphql.entity;

import java.util.List;

public class GraphQLEntityCollection {

    private final List<Class<?>> klasses;

    GraphQLEntityCollection(List<Class<?>> klasses) {
        this.klasses = klasses;
    }

    public List<Class<?>> getKlasses() {
        return klasses;
    }

}
