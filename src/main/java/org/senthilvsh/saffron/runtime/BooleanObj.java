package org.senthilvsh.saffron.runtime;

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
