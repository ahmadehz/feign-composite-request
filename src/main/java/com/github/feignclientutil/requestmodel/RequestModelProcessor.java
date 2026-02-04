package com.github.feignclientutil.requestmodel;

import com.github.feignclientutil.annotation.Body;
import com.github.feignclientutil.annotation.Header;
import com.github.feignclientutil.annotation.Param;
import com.github.feignclientutil.annotation.RequestModel;
import feign.MethodMetadata;
import org.springframework.cloud.openfeign.AnnotatedParameterProcessor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;

import static feign.Util.checkState;

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
        MethodMetadata metadata = context.getMethodMetadata();
        Class<?> paramClass = method.getParameterTypes()[paramIndex];

        boolean hasQueryParam = false;
        for (Field field : paramClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(Body.class)) {
                metadata.bodyIndex(paramIndex);
            }
            Header header = field.getAnnotation(Header.class);
            if (header != null) {
                String headerName = getHeaderName(field);

                if (Map.class.isAssignableFrom(field.getType())) {
                    metadata.headerMapIndex(paramIndex);
                } else {
                    metadata.template().header(headerName, "{"+field.getName()+"}");
                    metadata.indexToName()
                            .computeIfAbsent(paramIndex, k -> new ArrayList<>())
                            .add(field.getName());
                }
            }
            Param param = field.getAnnotation(Param.class);
            if (param != null) {
                hasQueryParam = true;
            }
        }

        if (hasQueryParam) {
            checkState(metadata.queryMapIndex() == null, "Query map can only be present once.");
            metadata.queryMapIndex(paramIndex);
            metadata.queryMapEncoder(new RequestModelQueryMapEncoder());
        }

        return true;
    }

    private String getHeaderName(Field field) {
        Header header = field.getAnnotation(Header.class);
        if (header != null && !header.value().isEmpty())
            return header.value();
        else return field.getName();
    }

    private String getParamName(Field field) {
        Param param = field.getAnnotation(Param.class);
        if (param != null && !param.value().isEmpty())
            return param.value();
        else return field.getName();
    }
}