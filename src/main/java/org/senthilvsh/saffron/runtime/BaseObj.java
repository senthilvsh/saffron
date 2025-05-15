package org.senthilvsh.saffron.runtime;

import org.senthilvsh.saffron.common.Type;

public class BaseObj {
    private final Type type;

    public BaseObj(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }
}
