package io.openfeign.extensions.compositerequest.binding;

import io.openfeign.extensions.compositerequest.annotation.Body;
import org.springframework.cloud.openfeign.AnnotatedParameterProcessor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import static feign.Util.checkState;

public class BodyFieldBinder implements CompositeRequestFieldBinder {

    @Override
    public Class<? extends Annotation> getAnnotationType() {
        return Body.class;
    }

    @Override
    public void bind(AnnotatedParameterProcessor.AnnotatedParameterContext context, Annotation annotation, Field field) {
        checkState(context.getMethodMetadata().bodyIndex() == null,
                "Method has too many Body parameters");

        context.getMethodMetadata().bodyIndex(context.getParameterIndex());
    }
}
