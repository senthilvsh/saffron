package org.senthilvsh.saffron.runtime;

import org.senthilvsh.saffron.common.Type;

public class NumberObj extends BaseObj {
    private final double value;

    public NumberObj(double value) {
        super(Type.NUMBER);
        this.value = value;
    }

    public double getValue() {
        return value;
    }
}
