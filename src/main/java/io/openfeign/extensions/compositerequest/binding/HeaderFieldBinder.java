package io.openfeign.extensions.compositerequest.binding;

import io.openfeign.extensions.compositerequest.annotation.Header;
import feign.MethodMetadata;
import org.springframework.cloud.openfeign.AnnotatedParameterProcessor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;

public class HeaderFieldBinder implements CompositeRequestFieldBinder {

    private static final Class<Header> ANNOTATION = Header.class;

    @Override
    public Class<? extends Annotation> getAnnotationType() {
        return ANNOTATION;
    }

    @Override
    public void bind(AnnotatedParameterProcessor.AnnotatedParameterContext context, Annotation annotation, Field field) {
        MethodMetadata metadata = context.getMethodMetadata();
        int paramIndex = context.getParameterIndex();
        String headerName = getHeaderName(field);

        metadata.template().header(headerName, "{" + field.getName() + "}");
        metadata.indexToName()
                .computeIfAbsent(paramIndex, k -> new ArrayList<>())
                .add(field.getName());

    }

    private String getHeaderName(Field field) {
        Header header = field.getAnnotation(ANNOTATION);
        if (header != null && !header.value().isEmpty())
            return header.value();
        else return field.getName();
    }
}
