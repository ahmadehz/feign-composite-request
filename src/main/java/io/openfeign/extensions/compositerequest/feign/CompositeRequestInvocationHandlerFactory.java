package io.openfeign.extensions.compositerequest.feign;

import feign.InvocationHandlerFactory;
import feign.Target;
import io.openfeign.extensions.compositerequest.annotation.CompositeRequest;
import io.openfeign.extensions.compositerequest.internal.CompositeRequestParts;
import io.openfeign.extensions.compositerequest.internal.CompositeArgumentLayout;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;

public final class CompositeRequestInvocationHandlerFactory
        implements InvocationHandlerFactory {

    private final InvocationHandlerFactory delegate;

    public CompositeRequestInvocationHandlerFactory() {
        this(new DefaultInvocationHandlerFactory());
    }

    public CompositeRequestInvocationHandlerFactory(InvocationHandlerFactory delegate) {
        this.delegate = delegate;
    }

    @Override
    public InvocationHandler create(Target target, Map<Method, MethodHandler> dispatch) {
        InvocationHandler base = delegate.create(target, dispatch);
        return new CompositeInvocationHandler(base, dispatch);
    }
}

final class CompositeInvocationHandler implements InvocationHandler {

    private final InvocationHandler delegate;
    private final Map<Method, InvocationHandlerFactory.MethodHandler> dispatch;

    CompositeInvocationHandler(InvocationHandler delegate,
                               Map<Method, InvocationHandlerFactory.MethodHandler> dispatch) {
        this.delegate = delegate;
        this.dispatch = dispatch;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        int compositeRequestParameterIndex = compositeRequestParameterIndex(method);
        if (compositeRequestParameterIndex < 0)
            return delegate.invoke(proxy, method, args);

        Object compositeObject = args[compositeRequestParameterIndex];
        CompositeRequestParts requestParts = CompositeRequestParts.from(compositeObject);
        CompositeArgumentLayout argumentLayout = CompositeArgumentLayout.from(method.getParameterCount(), requestParts.hasBody());

        Object[] rewrittenArgs = Arrays.copyOf(args, argumentLayout.totalParameterCount());
        rewrittenArgs[argumentLayout.headerIndex()] = requestParts.getHeaders();
        rewrittenArgs[argumentLayout.paramIndex()] = requestParts.getParams();
        if (requestParts.hasBody())
            rewrittenArgs[argumentLayout.bodyIndex()] = requestParts.getBody();

        return dispatch.get(method).invoke(rewrittenArgs);
    }

    private int compositeRequestParameterIndex(Method method) {
        for (int index = 0; method.getParameters().length > index; index++) {
            if (method.getParameters()[index].isAnnotationPresent(CompositeRequest.class))
                return index;
        }
        return -1;
    }
}

final class DefaultInvocationHandlerFactory implements InvocationHandlerFactory {

    @Override
    public InvocationHandler create(Target target, Map<Method, MethodHandler> dispatch) {
        return ((o, method, args) -> {
            if ("equals".equals(method.getName())) {
                try {
                    Object otherHandler =
                            args.length > 0 && args[0] != null ? Proxy.getInvocationHandler(args[0]) : null;
                    return equals(otherHandler);
                } catch (IllegalArgumentException e) {
                    return false;
                }
            } else if ("hashCode".equals(method.getName())) {
                return hashCode();
            } else if ("toString".equals(method.getName())) {
                return toString();
            } else if (!dispatch.containsKey(method)) {
                throw new UnsupportedOperationException(
                        String.format("Method \"%s\" should not be called", method.getName()));
            }

            return dispatch.get(method).invoke(args);
        });
    }
}