package com.example.graphql.service;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
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
import org.springframework.stereotype.Component;

import com.example.graphql.annotation.GraphQLMutable;
import com.example.graphql.annotation.GraphQLQueryable;
import com.example.graphql.factory.GraphQLSchemaFactory;
import com.example.graphql.util.AbstractGraphQLRegistrar;

/**
 * Scan all packages from {@link EnableGraphQLService}
 * and find all {@link GraphQLQueryable} and {@link GraphQLMutable} beans
 * and build a {@link GraphQLServiceCollection} for {@link GraphQLSchemaFactory}
 * to build {@link GraphQLSchema} dynamically.
 */
class GraphQLServiceRegistrar extends AbstractGraphQLRegistrar implements ImportBeanDefinitionRegistrar {

    private static final Class<? extends Annotation> queryable = GraphQLQueryable.class;
    private static final Class<? extends Annotation> mutable = GraphQLMutable.class;

    private final ClassPathScanningCandidateComponentProvider scanner;

    GraphQLServiceRegistrar(Environment environment) {
        super(environment);

        scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.setEnvironment(environment);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Component.class));
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        List<Class<?>> klasses = getPackagesToScan(EnableGraphQLService.class, metadata).stream()
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
            .filter(this::isGraphQLService)
            .collect(Collectors.toList());
        var definition = BeanDefinitionBuilder
            .genericBeanDefinition(GraphQLServiceCollection.class)
            .addConstructorArgValue(klasses)
            .getBeanDefinition();
        registry.registerBeanDefinition(GraphQLServiceCollection.class.getName(), definition);
    }

    private boolean isGraphQLService(Class<?> klass) {
        var annotation = klass.getAnnotation(queryable);
        if (annotation != null) {
            return true;
        }
        annotation = klass.getAnnotation(mutable);
        if (annotation != null) {
            return true;
        }
        try {
            var info = Introspector.getBeanInfo(klass);
            for (MethodDescriptor method : info.getMethodDescriptors()) {
                annotation = method.getMethod().getAnnotation(queryable);
                if (annotation != null) {
                    return true;
                }
                annotation = method.getMethod().getAnnotation(mutable);
                if (annotation != null) {
                    return true;
                }
            }
        } catch (IntrospectionException ignored) {
            return false;
        }

        return false;
    }

}
