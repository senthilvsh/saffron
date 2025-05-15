package org.senthilvsh.saffron.runtime;

import org.senthilvsh.saffron.common.Type;

public class BooleanObj extends BaseObj {
    private final boolean value;

    public BooleanObj(boolean value) {
        super(Type.BOOLEAN);
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }
}
