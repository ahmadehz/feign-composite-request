package com.github.feignclientutil.requestmodel;

import com.github.feignclientutil.annotation.Body;
import com.github.feignclientutil.annotation.Header;
import com.github.feignclientutil.annotation.RequestModel;
import feign.Contract;
import feign.MethodMetadata;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RequestModelContract implements Contract {


    private final Contract delegate;

    public RequestModelContract(Contract delegate) {
        this.delegate = delegate;
    }

    @Override
    public List<MethodMetadata> parseAndValidateMetadata(Class<?> targetType) {
        List<MethodMetadata> metadataList =
                delegate.parseAndValidateMetadata(targetType);

        for (MethodMetadata metadata : metadataList) {
            enhance(metadata);
        }

        return metadataList;
    }

    private void enhance(MethodMetadata data) {
        Method method = data.method();

        Annotation[][] paramAnnotations = method.getParameterAnnotations();
        Type[] paramTypes = method.getGenericParameterTypes();

        for (int i = 0; i < paramAnnotations.length; i++) {
            if (!hasRequestModel(paramAnnotations[i])) {
                continue;
            }

            Class<?> modelType = (Class<?>) paramTypes[i];
            processRequestModel(data, i, modelType);
        }
    }

    private void processRequestModel(MethodMetadata data,
                                     int paramIndex,
                                     Class<?> modelType) {

        for (Field field : modelType.getDeclaredFields()) {

            // BODY
            if (field.isAnnotationPresent(Body.class)) {
                data.bodyIndex(paramIndex);
            }

            // HEADER
            Header header = field.getAnnotation(Header.class);
            if (header != null) {

                String headerName = getHeaderName(field);

                if (Map.class.isAssignableFrom(field.getType())) {
                    data.headerMapIndex(paramIndex);
                } else {
                    data.template().header(headerName, "{"+field.getName()+"}");
                    data.indexToName()
                            .computeIfAbsent(paramIndex, k -> new ArrayList<>())
                            .add(field.getName());
                }
            }
        }
    }

    private boolean hasRequestModel(Annotation[] annotations) {
        for (Annotation a : annotations) {
            if (a.annotationType() == RequestModel.class) {
                return true;
            }
        }
        return false;
    }

    private String getHeaderName(Field field) {
        Header header = field.getAnnotation(Header.class);
        if (header != null && !header.value().isEmpty())
            return header.value();
        else return field.getName();
    }
}
