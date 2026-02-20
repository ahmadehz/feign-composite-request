package io.openfeign.extensions.compositerequest.internal;

import io.openfeign.extensions.compositerequest.annotation.Body;
import io.openfeign.extensions.compositerequest.annotation.Header;
import io.openfeign.extensions.compositerequest.annotation.Param;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class CompositeRequestParts {

    private final Map<String, Object> headers;

    private final Map<String, Object> params;

    private final Object body;

    private CompositeRequestParts(Map<String, Object> headers, Map<String, Object> params, Object body) {
        this.headers = headers;
        this.params = params;
        this.body = body;
    }

    public static CompositeRequestParts from(Object source) {
        Map<String, Object> headers = new LinkedHashMap<>();
        Map<String, Object> params = new LinkedHashMap<>();
        Object body = null;

        for (Field field : source.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Header.class)) {
                Header header = field.getAnnotation(Header.class);
                Object value = getValue(field, source);
                if (Map.class.isAssignableFrom(field.getType()))
                    headers.putAll((Map<String, ?>) value);
                else headers.put(getName(header.value(), field), value);
            }
            if (field.isAnnotationPresent(Param.class)) {
                Param param = field.getAnnotation(Param.class);
                Object value = getValue(field, source);
                if (Map.class.isAssignableFrom(field.getType()))
                    params.putAll((Map<String, ?>) value);
                else params.put(getName(param.value(), field), getValue(field, source));
            }
            if (field.isAnnotationPresent(Body.class)) {
                if (body != null)
                    throw new IllegalArgumentException("Body annotation cannot have more than once");
                body = getValue(field, source);
            }
        }
        return new CompositeRequestParts(headers, params, body);
    }

    public Map<String, Object> getHeaders() {
        return headers;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public Object getBody() {
        return body;
    }

    public boolean hasBody() {
        return body != null;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CompositeRequestParts that)) return false;
        return Objects.equals(headers, that.headers) && Objects.equals(params, that.params) && Objects.equals(body, that.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(headers, params, body);
    }

    @Override
    public String toString() {
        return "CompositeExtraction{" +
                "headers=" + headers +
                ", params=" + params +
                ", body=" + body +
                '}';
    }

    private static Object getValue(Field f, Object target) {
        try {
            f.setAccessible(true);
            return f.get(target);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    private static String getName(String value, Field field) {
        return value.isEmpty() ? field.getName() : value;
    }
}