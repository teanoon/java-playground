package com.example.graphql.factory;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableMap;

import graphql.Scalars;
import graphql.schema.DataFetcher;
import graphql.schema.FieldCoordinates;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLCodeRegistry;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLOutputType;
import graphql.schema.GraphQLSchema;

import lombok.AllArgsConstructor;
import lombok.Data;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.example.graphql.annotation.GraphQLMutable;
import com.example.graphql.annotation.GraphQLQueryable;
import com.example.graphql.entity.GraphQLEntityCollection;
import com.example.graphql.service.GraphQLServiceCollection;

/**
 * knows all types/services/methods and build a global schema containing all queries and mutations.
 */
@Component
public class GraphQLSchemaFactory implements FactoryBean<GraphQLSchema>, ApplicationContextAware {

    private static final String ROOT_QUERY_NAME = "query";
    private static final String ROOT_MUTATION_NAME = "mutation";

    private final GraphQLEntityCollection entityCollection;
    private final GraphQLServiceCollection serviceCollection;

    private Map<Class<?>, GraphQLOutputType> entityTypes = new HashMap<>();
    private Map<Class<?>, GraphQLInputType> inputTypes = new HashMap<>();
    private Map<Class<?>, Object> beans = new HashMap<>();
    private Map<String, RootTypeWrapper> queryTypes = new HashMap<>();
    private Map<String, RootTypeWrapper> mutationTypes = new HashMap<>();
    private Map<FieldCoordinates, DataFetcher<?>> dataFetchers = new HashMap<>();

    public GraphQLSchemaFactory(
            GraphQLEntityCollection entityCollection,
            GraphQLServiceCollection serviceCollection) {
        this.entityCollection = entityCollection;
        this.serviceCollection = serviceCollection;
    }

    @Override
    public Class<?> getObjectType() {
        return GraphQLSchema.class;
    }

    @Override
    public GraphQLSchema getObject() throws Exception {
        buildObjectTypes();
        buildRootTypes();
        var schemaBuilder = GraphQLSchema.newSchema().additionalTypes(new HashSet<>(entityTypes.values()));
        if (MapUtils.isNotEmpty(queryTypes)) {
            var queryBuilder = GraphQLObjectType.newObject().name(ROOT_QUERY_NAME);
            queryTypes.forEach((name, type) -> queryBuilder
                .field(GraphQLFieldDefinition
                    .newFieldDefinition()
                    .name(name)
                    .type(type.returnType)
                    .arguments(type.arguments)));
            schemaBuilder.query(queryBuilder);
        }
        if (MapUtils.isNotEmpty(mutationTypes)) {
            var mutationBuilder = GraphQLObjectType.newObject().name(ROOT_MUTATION_NAME);
            mutationTypes.forEach((name, type) -> mutationBuilder
                .field(GraphQLFieldDefinition
                    .newFieldDefinition()
                    .name(name)
                    .type(type.returnType)
                    .arguments(type.arguments)));
            schemaBuilder.mutation(mutationBuilder);
        }
        if (MapUtils.isNotEmpty(dataFetchers)) {
            var registryBuilder = GraphQLCodeRegistry.newCodeRegistry();
            dataFetchers.forEach((coordinates, dataFetcher) -> registryBuilder.dataFetcher(coordinates, dataFetcher));
            schemaBuilder.codeRegistry(registryBuilder.build());
        }
        return schemaBuilder.build();
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        serviceCollection.getKlasses().forEach(klass -> beans.put(klass, context.getBean(klass)));
    }

    private void buildObjectTypes() {
        entityCollection.getKlasses().stream()
            .forEach(klass -> entityTypes.put(klass, new EntityTypeBuilder(klass).build()));
        entityCollection.getKlasses().stream()
            .forEach(klass -> inputTypes.put(klass, new EntityInputTypeBuilder(klass).build()));
        ImmutableMap
            .of(
                Long.class, Scalars.GraphQLInt,
                Boolean.class, Scalars.GraphQLBoolean,
                Float.class, Scalars.GraphQLFloat,
                String.class, Scalars.GraphQLString
            )
            .forEach((klass, type) -> {
                entityTypes.put(klass, type);
                inputTypes.put(klass, type);
            });
    }

    private void buildRootTypes() {
        serviceCollection.getKlasses().stream()
            .flatMap(klass -> Stream.of(klass.getDeclaredMethods()))
            .filter(method -> Modifier.isPublic(method.getModifiers()) && !Modifier.isStatic(method.getModifiers()))
            .forEach(method -> {
                var mutation = method.getAnnotation(GraphQLMutable.class);
                var bean = beans.get(method.getDeclaringClass());
                if (mutation != null) {
                    var type = buildRootType(method, mutation.name());
                    mutationTypes.put(type.name, type);
                    buildDataFetchers(ROOT_MUTATION_NAME, bean, method, mutation.name());
                    return;
                }
                var query = method.getAnnotation(GraphQLQueryable.class);
                if (query == null) {
                    query = method.getDeclaringClass().getAnnotation(GraphQLQueryable.class);
                }
                if (query != null) {
                    var type = buildRootType(method, query.name());
                    queryTypes.put(type.name, type);
                    buildDataFetchers(ROOT_QUERY_NAME, bean, method, query.name());
                }
            });
    }

    private void buildDataFetchers(String parentName, Object bean, Method method, String annotationName) {
        var name = StringUtils.isNotBlank(annotationName) ? annotationName : method.getName();
        var fieldCoordinates = FieldCoordinates.coordinates(parentName, name);
        DataFetcher<?> dataFetcher = env -> {
            var arguments = Stream.of(method.getParameters())
                .map(parameter -> env.getArgument(parameter.getName()))
                .toArray();
            return method.invoke(bean, arguments);
        };
        dataFetchers.put(fieldCoordinates, dataFetcher);
    }

    private RootTypeWrapper buildRootType(Method method, String annotationName) {
        // TODO method name should be unique/no overloads
        var name = StringUtils.isNotBlank(annotationName) ? annotationName : method.getName();
        var returnType = entityTypes.get(method.getReturnType());
        var arguments = Stream.of(method.getParameters())
            .map(parameter -> {
                var argumentName = parameter.getName();
                var type = inputTypes.get(parameter.getType());
                return GraphQLArgument.newArgument().name(argumentName).type(type).build();
            })
            .collect(Collectors.toList());

        return new RootTypeWrapper(name, arguments, returnType);
    }

    @Data
    @AllArgsConstructor
    static class RootTypeWrapper {

        private String name;
        private List<GraphQLArgument> arguments;
        private GraphQLOutputType returnType;

    }

}
