package com.example.graphql.entity;

import java.beans.Introspector;
import java.util.stream.Stream;

import graphql.Scalars;
import graphql.schema.GraphQLObjectType;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.FactoryBean;

import com.example.graphql.annotation.GraphQLEntity;

import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;

/**
 * build {@link GraphQLObjectType} from the given entity class.
 */
class GraphQLTypeFactoryBean implements FactoryBean<GraphQLObjectType> {

    static String beanName(String beanClassName) {
        return String.format("graphql-schema-type-%s", beanClassName);
    }

    private final Class<?> entityClass;

    GraphQLTypeFactoryBean(Class<?> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public GraphQLObjectType getObject() throws Exception {
        var annotation = entityClass.getAnnotation(GraphQLEntity.class);
        var name = StringUtils.isNotBlank(annotation.name()) ? annotation.name() : entityClass.getSimpleName();
        var builder = newObject().name(name);
        var info = Introspector.getBeanInfo(entityClass);
        Stream.of(info.getPropertyDescriptors())
            .filter(property -> property.getReadMethod() != null && property.getWriteMethod() != null)
            .map(property -> newFieldDefinition()
                .name(property.getName())
                // TODO detect primitives/types/references
                .type(Scalars.GraphQLString)
                .build())
            .forEach(builder::field);
        return builder.build();
    }

    @Override
    public Class<?> getObjectType() {
        return GraphQLObjectType.class;
    }

}
