package com.github.feignclientutil.binding;

import org.springframework.cloud.openfeign.AnnotatedParameterProcessor.AnnotatedParameterContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public interface RequestModelFieldBinder {

    Class<? extends Annotation> getAnnotationType();

    void bind(AnnotatedParameterContext context, Annotation annotation, Field field);
}
