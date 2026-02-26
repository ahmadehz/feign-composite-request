package io.openfeign.extensions.compositerequest.feign;

import feign.Contract;
import feign.MethodMetadata;
import io.openfeign.extensions.compositerequest.internal.CompositeArgumentLayout;

import java.util.List;
import java.util.Objects;

import static io.openfeign.extensions.compositerequest.util.CompositeRequestUtil.*;

public final class CompositeRequestContract implements Contract {

    private final Contract delegate;

    public CompositeRequestContract(Contract delegate) {
        this.delegate = Objects.requireNonNull(delegate);
    }

    @Override
    public List<MethodMetadata> parseAndValidateMetadata(Class<?> targetType) {
        List<MethodMetadata> methodMetadataList = delegate.parseAndValidateMetadata(targetType);
        for (MethodMetadata metadata : methodMetadataList) {
            int index = getParameterIndex(metadata.method());
            if (index >= 0) {
                checkState(metadata);
                setCompositeRequestToMetadata(metadata, index);
            }
        }
        return methodMetadataList;
    }

    private void checkState(MethodMetadata metadata) {
        if (metadata.headerMapIndex() != null)
            throw new IllegalArgumentException("HeaderMapIndex already set");
        if (metadata.queryMapIndex() != null)
            throw new IllegalArgumentException("QueryMapIndex already set");
    }

    private void setCompositeRequestToMetadata(MethodMetadata metadata, int compositeRequestIndex) {
        CompositeArgumentLayout compositeArgumentLayout = CompositeArgumentLayout.from(metadata.method().getParameterCount());
        Class<?> type = metadata.method().getParameters()[compositeRequestIndex].getType();

        metadata.ignoreParamater(compositeRequestIndex);

        metadata.headerMapIndex(compositeArgumentLayout.headerIndex());
        metadata.queryMapIndex(compositeArgumentLayout.paramIndex());
        if (hasBody(type)) {
            metadata.bodyIndex(compositeArgumentLayout.bodyIndex());
            metadata.setBodyRequired(false);
        }
    }
}