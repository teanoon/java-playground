package com.example.graphql.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import graphql.GraphQL;
import graphql.execution.instrumentation.ChainedInstrumentation;
import graphql.execution.instrumentation.Instrumentation;
import graphql.schema.GraphQLCodeRegistry;
import graphql.schema.GraphQLSchema;
import graphql.schema.GraphQLTypeVisitor;
import graphql.schema.SchemaTraverser;
import graphql.schema.TypeResolver;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.TypeDefinitionRegistry;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.core.io.Resource;
import org.springframework.graphql.execution.DataFetcherExceptionResolver;
import org.springframework.graphql.execution.GraphQlSource;
import org.springframework.graphql.execution.GraphQlSource.Builder;
import org.springframework.graphql.execution.MissingSchemaException;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

public class EntityGraphQLSourceBuilder implements GraphQlSource.Builder {

    @Nullable
    private GraphQLSchema schema;
	private final List<RuntimeWiringConfigurer> runtimeWiringConfigurers = new ArrayList<>();
	@Nullable
	private TypeResolver defaultTypeResolver;
	private final List<DataFetcherExceptionResolver> exceptionResolvers = new ArrayList<>();
	private final List<GraphQLTypeVisitor> typeVisitors = new ArrayList<>();
	private final List<Instrumentation> instrumentations = new ArrayList<>();
	private Consumer<GraphQL.Builder> graphQlConfigurers = (builder) -> {};

    @Override
    public Builder schemaResources(Resource... resources) {
        throw new UnsupportedOperationException("schema will be built by @GraphQLEntityScan.");
    }

    public Builder schema(GraphQLSchema schema) {
        this.schema = schema;
        return this;
    }

    @Override
    public Builder configureRuntimeWiring(RuntimeWiringConfigurer configurer) {
        runtimeWiringConfigurers.add(configurer);
        return this;
    }

    @Override
    public Builder defaultTypeResolver(TypeResolver typeResolver) {
        defaultTypeResolver = typeResolver;
        return this;
    }

    @Override
    public Builder exceptionResolvers(List<DataFetcherExceptionResolver> resolvers) {
        exceptionResolvers.addAll(resolvers);
        return this;
    }

    @Override
    public Builder typeVisitors(List<GraphQLTypeVisitor> typeVisitors) {
        this.typeVisitors.addAll(typeVisitors);
        return this;
    }

    @Override
    public Builder instrumentation(List<Instrumentation> instrumentations) {
        this.instrumentations.addAll(instrumentations);
        return this;
    }

    @Override
    public Builder schemaFactory(BiFunction<TypeDefinitionRegistry, RuntimeWiring, GraphQLSchema> schemaFactory) {
        throw new UnsupportedOperationException("schema will be built by @GraphQLEntityScan.");
    }

    @Override
    public Builder configureGraphQl(Consumer<graphql.GraphQL.Builder> configurer) {
		this.graphQlConfigurers = this.graphQlConfigurers.andThen(configurer);
        return this;
    }

    @Override
    public GraphQlSource build() {
        if (schema == null) {
            throw new MissingSchemaException();
        }

        var schemaBuilder = GraphQLSchema.newSchema(this.schema);
        if (CollectionUtils.isNotEmpty(runtimeWiringConfigurers)) {
            var runtimeWiringBuilder = RuntimeWiring.newRuntimeWiring();
            runtimeWiringConfigurers.forEach(configurer -> configurer.configure(runtimeWiringBuilder));
            var runtimeWiring = runtimeWiringBuilder.build();
            // add default type resolver
            schemaBuilder.codeRegistry(runtimeWiring.getCodeRegistry());
        }
        var schema = schemaBuilder.build();
        schema = applyTypeVisitors(schema);

        var graphQLBuilder = GraphQL.newGraphQL(schema);
		// graphQLBuilder.defaultDataFetcherExceptionHandler(new ExceptionResolversExceptionHandler(this.exceptionResolvers));
        if (!this.instrumentations.isEmpty()) {
			graphQLBuilder = graphQLBuilder.instrumentation(new ChainedInstrumentation(this.instrumentations));
		}
		this.graphQlConfigurers.accept(graphQLBuilder);
        var graphQL = graphQLBuilder.build();

        return new SimpleGraphQlSource(graphQL, schema);
    }

	private GraphQLSchema applyTypeVisitors(GraphQLSchema schema) {
		List<GraphQLTypeVisitor> visitors = new ArrayList<>(this.typeVisitors);
		// visitors.add(ContextDataFetcherDecorator.TYPE_VISITOR);

		GraphQLCodeRegistry.Builder codeRegistry = GraphQLCodeRegistry.newCodeRegistry(schema.getCodeRegistry());
		Map<Class<?>, Object> vars = Collections.singletonMap(GraphQLCodeRegistry.Builder.class, codeRegistry);

		SchemaTraverser traverser = new SchemaTraverser();
		traverser.depthFirstFullSchema(visitors, schema, vars);

		return schema.transformWithoutTypes(builder -> builder.codeRegistry(codeRegistry));
	}

    private static class SimpleGraphQlSource implements GraphQlSource {

		private final GraphQL graphQl;

		private final GraphQLSchema schema;

		SimpleGraphQlSource(GraphQL graphQl, GraphQLSchema schema) {
			this.graphQl = graphQl;
			this.schema = schema;
		}

		@Override
		public GraphQL graphQl() {
			return this.graphQl;
		}

		@Override
		public GraphQLSchema schema() {
			return this.schema;
		}

    }

}
