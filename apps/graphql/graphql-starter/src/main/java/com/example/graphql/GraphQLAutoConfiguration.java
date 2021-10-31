package com.example.graphql;

import java.util.stream.Collectors;

import graphql.execution.instrumentation.Instrumentation;
import graphql.schema.GraphQLSchema;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.boot.GraphQlProperties;
import org.springframework.graphql.boot.GraphQlSourceBuilderCustomizer;
import org.springframework.graphql.execution.DataFetcherExceptionResolver;
import org.springframework.graphql.execution.GraphQlSource;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

import com.example.graphql.entity.EntityGraphQLSourceBuilder;
import com.example.graphql.example.GraphQLExampleService;
import com.example.graphql.factory.GraphQLSchemaFactory;

@Configuration
@ComponentScan(basePackageClasses = {GraphQLExampleService.class, GraphQLSchemaFactory.class})
@EnableConfigurationProperties(GraphQlProperties.class)
public class GraphQLAutoConfiguration {

    @Bean
    public GraphQlSource graphQlSource(
            GraphQLSchema schema,
			ObjectProvider<DataFetcherExceptionResolver> exceptionResolversProvider,
			ObjectProvider<Instrumentation> instrumentationsProvider,
			ObjectProvider<GraphQlSourceBuilderCustomizer> sourceCustomizers,
			ObjectProvider<RuntimeWiringConfigurer> wiringConfigurers) {
        var builder = new EntityGraphQLSourceBuilder()
            .schema(schema)
            .exceptionResolvers(exceptionResolversProvider.orderedStream().collect(Collectors.toList()))
            .instrumentation(instrumentationsProvider.orderedStream().collect(Collectors.toList()));
        sourceCustomizers.orderedStream().forEach((customizer) -> customizer.customize(builder));
        wiringConfigurers.orderedStream().forEach(builder::configureRuntimeWiring);
        return builder.build();
    }

}
