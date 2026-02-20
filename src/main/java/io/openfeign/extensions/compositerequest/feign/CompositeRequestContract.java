package io.openfeign.extensions.compositerequest.feign;

import feign.Contract;
import feign.MethodMetadata;
import io.openfeign.extensions.compositerequest.annotation.Body;
import io.openfeign.extensions.compositerequest.annotation.CompositeRequest;
import io.openfeign.extensions.compositerequest.internal.CompositeArgumentLayout;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.List;

public final class CompositeRequestContract implements Contract {

    private final Contract delegate;

    public CompositeRequestContract(Contract delegate) {
        this.delegate = delegate;
    }

    @Override
    public List<MethodMetadata> parseAndValidateMetadata(Class<?> targetType) {
        List<MethodMetadata> methodMetadataList = delegate.parseAndValidateMetadata(targetType);
        for (MethodMetadata metadata : methodMetadataList) {
            int index = compositeRequestIndex(metadata);
            if (index >= 0) {
                checkState(metadata);
                setCompositeRequestToMetadata(metadata, index);
            }
        }
        return methodMetadataList;
    }

    private int compositeRequestIndex(MethodMetadata metadata) {
        int totalParameters = metadata.method().getParameterCount();
        for (int index = 0; index < totalParameters; index++) {
            Parameter parameter = metadata.method().getParameters()[index];
            if (parameter.isAnnotationPresent(CompositeRequest.class))
                return index;
        }
        return -1;
    }

    private void checkState(MethodMetadata metadata) {
        if (metadata.headerMapIndex() != null)
            throw new IllegalArgumentException("HeaderMapIndex already set");
        if (metadata.queryMapIndex() != null)
            throw new IllegalArgumentException("QueryMapIndex already set");
    }

    private void setCompositeRequestToMetadata(MethodMetadata metadata, int compositeRequestIndex) {
        CompositeArgumentLayout compositeArgumentLayout = CompositeArgumentLayout.from(metadata.method().getParameterCount());
        metadata.ignoreParamater(compositeRequestIndex);

        metadata.headerMapIndex(compositeArgumentLayout.headerIndex());
        metadata.queryMapIndex(compositeArgumentLayout.paramIndex());
        if (hasBody(metadata, compositeRequestIndex))
            metadata.bodyIndex(compositeArgumentLayout.bodyIndex());
    }

    private boolean hasBody(MethodMetadata metadata, int compositeRequestIndex) {
        Parameter compositerParameter = metadata.method().getParameters()[compositeRequestIndex];
        for (Field field : compositerParameter.getType().getDeclaredFields()) {
            if (field.isAnnotationPresent(Body.class))
                return true;
        }
        return false;
    }
}