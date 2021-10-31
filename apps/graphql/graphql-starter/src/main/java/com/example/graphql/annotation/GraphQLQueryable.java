package com.example.graphql.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark all services/service methods to make them become part of the query of the schema.
 */
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface GraphQLQueryable {

	/**
	 * (Optional) The query name. Defaults to the unqualified
	 * name of the service class plus the method name.
     * This name is used to refer to the query type in the schema.
	 */
	String name() default "";

}
