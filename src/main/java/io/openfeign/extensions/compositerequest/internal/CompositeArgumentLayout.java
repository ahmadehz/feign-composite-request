package io.openfeign.extensions.compositerequest.internal;

public final class CompositeArgumentLayout {

    private final int baseParameters;
    private final int headerIndex;
    private final int paramIndex;
    private final int bodyIndex;
    private final boolean hasBody;

    private CompositeArgumentLayout(int baseParameters, boolean hasBody) {
        this.baseParameters = baseParameters;
        this.headerIndex = baseParameters;
        this.paramIndex = baseParameters + 1;
        this.bodyIndex = baseParameters + 2;
        this.hasBody = hasBody;
    }

    public static CompositeArgumentLayout from(int parameters) {
        return from(parameters, true);
    }

    public static CompositeArgumentLayout from(int parameters, boolean hasBody) {
        return new CompositeArgumentLayout(parameters, hasBody);
    }


    public int totalParameterCount() {
        return hasBody ? baseParameters + 3 : baseParameters + 2;
    }

    public int bodyIndex() {
        if (!hasBody)
            throw new IllegalStateException("Body index has not been set");
        return bodyIndex;
    }

    public int headerIndex() {
        return headerIndex;
    }

    public int paramIndex() {
        return paramIndex;
    }
}