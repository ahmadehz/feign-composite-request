package io.openfeign.extensions.compositerequest.binding;

import io.openfeign.extensions.compositerequest.annotation.CompositeRequest;
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

public class CompositeRequestParameterProcessor implements AnnotatedParameterProcessor {

    private final Map<Class<? extends Annotation>, CompositeRequestFieldBinder> binders;

    public CompositeRequestParameterProcessor(List<CompositeRequestFieldBinder> compositeRequestFieldBinders) {
        List<CompositeRequestFieldBinder> binderList = getDefaultBinders();
        binderList.addAll(compositeRequestFieldBinders);
        this.binders = new HashMap<>();
        for (CompositeRequestFieldBinder binder : binderList) {
            binders.put(binder.getAnnotationType(), binder);
        }
    }

    @Override
    public Class<? extends Annotation> getAnnotationType() {
        return CompositeRequest.class;
    }

    @Override
    public boolean processArgument(AnnotatedParameterContext context,
                                   Annotation annotation,
                                   Method method) {
        int paramIndex = context.getParameterIndex();
        Class<?> paramClass = method.getParameterTypes()[paramIndex];

        for (Field field : paramClass.getDeclaredFields()) {
            for (Annotation fieldAnnotation : field.getAnnotations()) {
                CompositeRequestFieldBinder binder = binders.get(fieldAnnotation.annotationType());
                if (binder != null)
                    binder.bind(context, fieldAnnotation, field);
            }
        }
        return true;
    }

    private List<CompositeRequestFieldBinder> getDefaultBinders() {
        List<CompositeRequestFieldBinder> defaultBinders = new ArrayList<>();
        defaultBinders.add(new BodyFieldBinder());
        defaultBinders.add(new HeaderFieldBinder());
        defaultBinders.add(new ParamFieldBinder());
        return defaultBinders;
    }
}