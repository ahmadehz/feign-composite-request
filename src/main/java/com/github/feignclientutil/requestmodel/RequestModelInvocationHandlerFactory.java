package com.github.feignclientutil.requestmodel;

import com.github.feignclientutil.annotation.Body;
import com.github.feignclientutil.annotation.RequestModel;
import com.github.feignclientutil.dto.RequestDto;
import feign.InvocationHandlerFactory;
import feign.Target;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

@Component
public class RequestModelInvocationHandlerFactory implements InvocationHandlerFactory {

    @Override
    public InvocationHandler create(Target target,
                                    Map<Method, MethodHandler> dispatch) {

        return new RequestModelInvocationHandler(dispatch);
    }

    private static class RequestModelInvocationHandler implements InvocationHandler {

        private final Map<Method, MethodHandler> dispatch;

        RequestModelInvocationHandler(Map<Method, MethodHandler> dispatch) {
            this.dispatch = dispatch;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

            for (int i = 0; i < args.length; i++) {

                if (method.getParameterAnnotations()[i].length == 0)
                    continue;

                if (hasRequestModel(method.getParameterAnnotations()[i])) {

                    RequestDto dto = (RequestDto) args[i];

                    Object body = extractBody(dto);
                    args[i] = body;
                }
            }
            return dispatch.get(method).invoke(args);
        }

        private boolean hasRequestModel(Annotation[] annotations) {
            for (Annotation a : annotations) {
                if (a.annotationType() == RequestModel.class)
                    return true;
            }
            return false;
        }

        private Object extractBody(RequestDto dto) throws IllegalAccessException {
            for (Field f : dto.getClass().getDeclaredFields()) {
                if (f.isAnnotationPresent(Body.class)) {
                    f.setAccessible(true);
                    return f.get(dto);
                }
            }
            return null;
        }
    }
}

