package io.openfeign.extensions.compositerequest.internal;

import io.openfeign.extensions.compositerequest.annotation.Body;
import io.openfeign.extensions.compositerequest.annotation.Header;
import io.openfeign.extensions.compositerequest.annotation.Param;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;

public record CompositeRequestParts(MultiValueMap<String, Object> headers,
                                    MultiValueMap<String, Object> params,
                                    Object body) {

    public CompositeRequestParts {
        Objects.requireNonNull(headers, "headers must not be null");
        Objects.requireNonNull(params, "params must not be null");
    }

    public static CompositeRequestParts from(Object source) {
        return new Builder(source).build();
    }

    public boolean hasBody() {
        return body != null;
    }
}

class Builder {

    private final MultiValueMap<String, Object> headers = new LinkedMultiValueMap<>();
    private final MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
    private Object body;

    public Builder(Object source) {
        for (Field field : source.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Header.class)) {
                Header header = field.getAnnotation(Header.class);
                Object value = getValue(field, source);
                if (Map.class.isAssignableFrom(field.getType()))
                    addHeaders((Map<?, ?>) value);
                else headers.add(getName(header.value(), field), value);
            }
            if (field.isAnnotationPresent(Param.class)) {
                Param param = field.getAnnotation(Param.class);
                Object value = getValue(field, source);
                if (Map.class.isAssignableFrom(field.getType())) {
                    addParams((Map<?, ?>) value);
                } else params.add(getName(param.value(), field), value);
            }
            if (field.isAnnotationPresent(Body.class)) {
                if (body != null)
                    throw new IllegalArgumentException("Body annotation cannot have more than once");
                body = getValue(field, source);
            }
        }
    }

    public CompositeRequestParts build() {
        return new CompositeRequestParts(headers, params, body);
    }

    private Object getValue(Field f, Object target) {
        try {
            f.setAccessible(true);
            return f.get(target);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    private String getName(String value, Field field) {
        return value.isEmpty() ? field.getName() : value;
    }

    private void addHeaders(Map<?, ?> map) {
        addMap(map, this.headers);
    }

    private void addParams(Map<?, ?> map) {
        addMap(map, this.params);
    }

    private void addMap(Map<?, ?> source, MultiValueMap<String, Object> destination) {
        for (Map.Entry<?, ?> entry : source.entrySet()) {
            String key = validateParamName(entry.getKey());
            if (entry.getValue() instanceof Iterable<?> itr) {
                for (Object item : itr)
                    destination.add(key, item);
            } else destination.add(key, entry.getValue());
        }
    }

    private String validateParamName(Object value) {
        if (value == null)
            throw new IllegalArgumentException("Param must not be null.");
        if (!(value instanceof String))
            throw new IllegalArgumentException("Param should be a string. object type: " + value.getClass());
        return (String) value;
    }
}