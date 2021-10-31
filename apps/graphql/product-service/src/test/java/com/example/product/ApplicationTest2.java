package com.example.product;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.beans.ParameterDescriptor;

import org.junit.jupiter.api.Test;

import com.example.product.service.ProductServiceImpl;

public class ApplicationTest2 {

    @Test
    public void test() throws IntrospectionException {
        var info = Introspector.getBeanInfo(ProductServiceImpl.class);
        for (MethodDescriptor descriptor : info.getMethodDescriptors()) {
            if (descriptor == null || descriptor.getParameterDescriptors() == null) {
                continue;
            }
            for (ParameterDescriptor parameterDescriptor : descriptor.getParameterDescriptors()) {
                System.out.println(parameterDescriptor);
            }
        }
    }

}
