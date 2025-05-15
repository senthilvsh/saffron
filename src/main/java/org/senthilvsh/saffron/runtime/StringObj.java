package org.senthilvsh.saffron.runtime;

import org.senthilvsh.saffron.common.Type;

public class StringObj extends BaseObj {
    private final String value;

    public StringObj(String value) {
        super(Type.STRING);
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
