package org.senthilvsh.saffron.common;

import org.senthilvsh.saffron.runtime.BaseObj;

public class Variable {
    private final String name;
    private final Type type;
    private BaseObj value;
    private final boolean currentScope;

    public Variable(String name, Type type, BaseObj value, boolean currentScope) {
        this.name = name;
        this.type = type;
        this.value = value;
        this.currentScope = currentScope;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public BaseObj getValue() {
        return value;
    }

    public void setValue(BaseObj value) {
        this.value = value;
    }

    public boolean isCurrentScope() {
        return currentScope;
    }
}
