package io.openfeign.extensions.compositerequest.feign;

import io.openfeign.extensions.compositerequest.annotation.Body;
import feign.RequestTemplate;
import feign.codec.EncodeException;
import feign.codec.Encoder;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

public class CompositeRequestBodyEncoder implements Encoder {

    private final Encoder delegate;

    public CompositeRequestBodyEncoder(Encoder delegate) {
        this.delegate = delegate;
    }

    @Override
    public void encode(Object object, Type bodyType, RequestTemplate template)
            throws EncodeException {
        if (object == null) {
            delegate.encode(null, bodyType, template);
            return;
        }

        Class<?> clazz = object.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Body.class)) {
                field.setAccessible(true);
                try {
                    Object bodyValue = field.get(object);
                    delegate.encode(bodyValue, field.getGenericType(), template);
                    return;
                } catch (IllegalAccessException e) {
                    throw new EncodeException("Cannot encode body of class: " + clazz.getName(), e);
                }
            }
        }

        // fallback
        delegate.encode(object, bodyType, template);
    }
}