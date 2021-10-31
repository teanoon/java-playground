package com.example.graphql.example;

import graphql.ExecutionInput;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;

import org.springframework.stereotype.Component;

@Component
public class GraphQLExampleService {

    public <T> String query(String query, GraphQLSchema graphQLSchema) {
        var graphQL= GraphQL.newGraphQL(graphQLSchema).build();
        var execution = ExecutionInput.newExecutionInput(query);
        var result = graphQL.execute(execution);

        return result.getData().toString();
    }

}
