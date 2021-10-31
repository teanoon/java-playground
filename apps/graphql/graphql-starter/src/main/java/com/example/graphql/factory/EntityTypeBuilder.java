package com.example.graphql.factory;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.util.stream.Stream;

import graphql.Scalars;
import graphql.schema.GraphQLObjectType;

import org.apache.commons.lang3.StringUtils;

import com.example.graphql.annotation.GraphQLEntity;

import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;

class EntityTypeBuilder {

    private final Class<?> klass;

    EntityTypeBuilder(Class<?> klass) {
        this.klass = klass;
    }

    GraphQLObjectType build() {
        var annotation = klass.getAnnotation(GraphQLEntity.class);
        var name = StringUtils.isNotBlank(annotation.name()) ? annotation.name() : klass.getSimpleName();
        var builder = newObject().name(name);
        try {
            var info = Introspector.getBeanInfo(klass);
            Stream.of(info.getPropertyDescriptors())
                // find a standard pojo field
                .filter(property -> property.getReadMethod() != null && property.getWriteMethod() != null)
                .map(property -> newFieldDefinition()
                    .name(property.getName())
                    // TODO detect primitives/types/references
                    .type(Scalars.GraphQLString)
                    .build())
                .forEach(builder::field);
            return builder.build();
        } catch (IntrospectionException exp) {
            throw new IllegalStateException(exp);
        }
    }

}
