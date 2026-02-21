package io.openfeign.extensions.compositerequest.util;

import feign.MethodMetadata;
import io.openfeign.extensions.compositerequest.annotation.Body;
import io.openfeign.extensions.compositerequest.annotation.CompositeRequest;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class CompositeRequestUtil {
    private CompositeRequestUtil() {
    }

    public static int getParameterIndex(Method method) {
        int totalParameters = method.getParameterCount();
        for (int index = 0; index < totalParameters; index++) {
            Parameter parameter = method.getParameters()[index];
            if (parameter.isAnnotationPresent(CompositeRequest.class))
                return index;
        }
        return -1;
    }

    public static boolean hasBody(Class<?> clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Body.class))
                return true;
        }
        return false;
    }
}
