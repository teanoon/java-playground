package com.example.graphql.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark a POJO to become a GraphQL compatible type.
 *
 * Only fields with standard getters and setters are counted as part of the GraphQL schema.
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface GraphQLEntity {

	/**
	 * (Optional) The entity name. Defaults to the unqualified
	 * name of the entity class. This name is used to refer to the
	 * entity in queries.
	 */
	String name() default "";

}
