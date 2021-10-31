package com.example.graphql.entity;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import graphql.schema.GraphQLSchema;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import com.example.graphql.annotation.GraphQLEntity;
import com.example.graphql.factory.GraphQLSchemaFactory;
import com.example.graphql.util.AbstractGraphQLRegistrar;

/**
 * scan all packages specified by {@link GraphQLEntityScan}
 * and collect all entities marked with {@link GraphQLEntity}
 * and build a {@link GraphQLEntityCollection} for {@link GraphQLSchemaFactory}
 * to build {@link GraphQLSchema} dynamically.
 */
class GraphQLEntityRegistrar extends AbstractGraphQLRegistrar implements ImportBeanDefinitionRegistrar {

    private static final Class<? extends Annotation> annotation = GraphQLEntity.class;

    private final ClassPathScanningCandidateComponentProvider scanner;

    GraphQLEntityRegistrar(Environment environment) {
        super(environment);

        scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.setEnvironment(environment);
        scanner.addIncludeFilter(new AnnotationTypeFilter(annotation));
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        List<Class<?>> klasses = getPackagesToScan(GraphQLEntityScan.class, metadata).stream()
            .map(scanner::findCandidateComponents)
            .flatMap(Set::stream)
            .map(candidate -> candidate.getBeanClassName())
            .map(candidate -> {
                try {
                    return Class.forName(candidate);
                } catch (ClassNotFoundException ignored) {
                    return null;
                }
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        var definition = BeanDefinitionBuilder
            .genericBeanDefinition(GraphQLEntityCollection.class)
            .addConstructorArgValue(klasses)
            .getBeanDefinition();
        registry.registerBeanDefinition(GraphQLEntityCollection.class.getName(), definition);
    }

}
