package com.github.feignclientutil.binding;

import com.github.feignclientutil.annotation.RequestModel;
import org.springframework.cloud.openfeign.AnnotatedParameterProcessor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RequestModelParameterProcessor implements AnnotatedParameterProcessor {

    private final Map<Class<? extends Annotation>, RequestModelFieldBinder> binders;

    public RequestModelParameterProcessor(List<RequestModelFieldBinder> requestModelFieldBinders) {
        List<RequestModelFieldBinder> binderList = getDefaultBinders();
        binderList.addAll(requestModelFieldBinders);
        this.binders = new HashMap<>();
        for (RequestModelFieldBinder binder : binderList) {
            binders.put(binder.getAnnotationType(), binder);
        }
    }

    @Override
    public Class<? extends Annotation> getAnnotationType() {
        return RequestModel.class;
    }

    @Override
    public boolean processArgument(AnnotatedParameterContext context,
                                   Annotation annotation,
                                   Method method) {
        int paramIndex = context.getParameterIndex();
        Class<?> paramClass = method.getParameterTypes()[paramIndex];

        for (Field field : paramClass.getDeclaredFields()) {
            for (Annotation fieldAnnotation : field.getAnnotations()) {
                RequestModelFieldBinder binder = binders.get(fieldAnnotation.annotationType());
                if (binder != null)
                    binder.bind(context, fieldAnnotation, field);
            }
        }
        return true;
    }

    private List<RequestModelFieldBinder> getDefaultBinders() {
        List<RequestModelFieldBinder> defaultBinders = new ArrayList<>();
        defaultBinders.add(new BodyFieldBinder());
        defaultBinders.add(new HeaderFieldBinder());
        defaultBinders.add(new ParamFieldBinder());
        return defaultBinders;
    }
}