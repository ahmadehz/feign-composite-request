package com.github.feignclientutil.requestmodel;

import com.github.feignclientutil.annotation.Body;
import com.github.feignclientutil.annotation.RequestModel;
import feign.MethodMetadata;
import org.springframework.cloud.openfeign.AnnotatedParameterProcessor;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

@Component
public class RequestModelProcessor implements AnnotatedParameterProcessor {

    @Override
    public Class<? extends Annotation> getAnnotationType() {
        return RequestModel.class;
    }

    @Override
    public boolean processArgument(AnnotatedParameterContext context,
                                   Annotation annotation,
                                   Method method) {
        int paramIndex = context.getParameterIndex();
        MethodMetadata methodMetadata = context.getMethodMetadata();
        Class<?> paramType = method.getParameterTypes()[paramIndex];

        for (Field field : paramType.getDeclaredFields()) {
            if (field.isAnnotationPresent(Body.class)) {
                methodMetadata.bodyIndex(paramIndex);
            }
        }

        return true;
    }
}