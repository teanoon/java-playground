package com.example.graphql.factory;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.util.stream.Stream;

import graphql.Scalars;
import graphql.schema.GraphQLInputObjectField;
import graphql.schema.GraphQLInputObjectType;

import org.apache.commons.lang3.StringUtils;

import com.example.graphql.annotation.GraphQLEntity;

class EntityInputTypeBuilder {

    private final Class<?> klass;

    EntityInputTypeBuilder(Class<?> klass) {
        this.klass = klass;
    }

    GraphQLInputObjectType build() {
        var annotation = klass.getAnnotation(GraphQLEntity.class);
        var name = StringUtils.isNotBlank(annotation.name()) ? annotation.name() : klass.getSimpleName();
        var builder = GraphQLInputObjectType.newInputObject().name(name + "Input");
        try {
            var info = Introspector.getBeanInfo(klass);
            Stream.of(info.getPropertyDescriptors())
                // find a standard pojo field
                .filter(property -> property.getReadMethod() != null && property.getWriteMethod() != null)
                .map(property -> GraphQLInputObjectField.newInputObjectField()
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
