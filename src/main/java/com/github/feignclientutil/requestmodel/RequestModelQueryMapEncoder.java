package com.github.feignclientutil.requestmodel;

import com.github.feignclientutil.annotation.Param;
import feign.QueryMapEncoder;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class RequestModelQueryMapEncoder implements QueryMapEncoder {

    @Override
    public Map<String, Object> encode(Object object) {
        Map<String, Object> params = new HashMap<>();
        for (Field field : object.getClass().getDeclaredFields()) {
            Param param = field.getAnnotation(Param.class);
            if (param != null) {
                field.setAccessible(true);
                try {
                    String paramName = getName(field, param.value());
                    if (Map.class.isAssignableFrom(field.getType())) {
                        params.putAll((Map<String, ?>) field.get(object));
                    }
                    else params.put(paramName, field.get(object));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return params;
    }

    private String getName(Field field, String annotationValue) {
        return annotationValue != null && !annotationValue.isEmpty() ? annotationValue : field.getName();
    }
}
