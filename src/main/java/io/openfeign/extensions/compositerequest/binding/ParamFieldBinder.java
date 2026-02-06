package io.openfeign.extensions.compositerequest.binding;

import io.openfeign.extensions.compositerequest.annotation.Param;
import feign.MethodMetadata;
import feign.QueryMapEncoder;
import org.springframework.cloud.openfeign.AnnotatedParameterProcessor.AnnotatedParameterContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static feign.Util.checkState;

public class ParamFieldBinder implements CompositeRequestFieldBinder {

    private static final Class<Param> ANNOTATION = Param.class;

    @Override
    public Class<? extends Annotation> getAnnotationType() {
        return ANNOTATION;
    }

    @Override
    public void bind(AnnotatedParameterContext context, Annotation annotation, Field field) {
        MethodMetadata metadata = context.getMethodMetadata();
        int paramIndex = context.getParameterIndex();

        if (isHandledByThisClass(metadata))
            return;

        checkState(metadata.queryMapIndex() == null, "Query map can only be present once.");
        metadata.queryMapIndex(paramIndex);
        metadata.queryMapEncoder(new ParamFieldEncoder());
    }

    private boolean isHandledByThisClass(MethodMetadata metadata) {
        return metadata.queryMapEncoder() instanceof ParamFieldEncoder;
    }

    private String getParamName(Field field) {
        Param param = field.getAnnotation(ANNOTATION);
        if (param != null && !param.value().isEmpty())
            return param.value();
        else return field.getName();
    }

    private class ParamFieldEncoder implements QueryMapEncoder {

        @Override
        public Map<String, Object> encode(Object object) {
            Map<String, Object> params = new HashMap<>();
            for (Field field : object.getClass().getDeclaredFields()) {
                Param param = field.getAnnotation(ANNOTATION);
                if (param != null) {
                    field.setAccessible(true);
                    try {
                        String paramName = getParamName(field);
                        if (Map.class.isAssignableFrom(field.getType())) {
                            params.putAll((Map<String, ?>) field.get(object));
                        } else params.put(paramName, field.get(object));
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            return params;
        }

    }
}
