package com.example.graphql.util;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;

public abstract class AbstractGraphQLRegistrar {

    private final Environment environment;

    public AbstractGraphQLRegistrar(Environment environment) {
        this.environment = environment;
    }

    protected Set<String> getPackagesToScan(Class<? extends Annotation> annotation, AnnotationMetadata metadata) {
        var attributes = AnnotationAttributes.fromMap(metadata.getAnnotationAttributes(annotation.getName()));
        var packagesToScan = new LinkedHashSet<String>();
        for (String basePackage : attributes.getStringArray("basePackages")) {
            addResolvedPackage(basePackage, packagesToScan);
        }
        for (Class<?> basePackageClass : attributes.getClassArray("basePackageClasses")) {
            addResolvedPackage(ClassUtils.getPackageName(basePackageClass), packagesToScan);
        }
        if (packagesToScan.isEmpty()) {
            var packageName = ClassUtils.getPackageName(metadata.getClassName());
            Assert.state(StringUtils.isNotBlank(packageName), "@EntityScan cannot be used with the default package");
            return Collections.singleton(packageName);
        }
        return packagesToScan;
    }

    private void addResolvedPackage(String packageName, Set<String> packagesToScan) {
        packagesToScan.add(this.environment.resolvePlaceholders(packageName));
    }

}
