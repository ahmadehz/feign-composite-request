package io.openfeign.extensions.compositerequest.binding;

import org.springframework.cloud.openfeign.AnnotatedParameterProcessor.AnnotatedParameterContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public interface CompositeRequestFieldBinder {

    Class<? extends Annotation> getAnnotationType();

    void bind(AnnotatedParameterContext context, Annotation annotation, Field field);
}
