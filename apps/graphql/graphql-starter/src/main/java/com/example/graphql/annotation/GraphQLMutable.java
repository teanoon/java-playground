package com.example.graphql.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark all service methods to make them become part of the mutation of the schema.
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface GraphQLMutable {

	/**
	 * (Optional) The mutation name. Defaults to the unqualified
	 * name of the service class plus the method name.
     * This name is used to refer to the mutation type in the schema.
	 */
	String name() default "";

}
