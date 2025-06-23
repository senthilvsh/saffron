package org.senthilvsh.saffron.runtime;

public class Variable {
    private final String name;
    private Object value;
    private final int scopeDepth;

    public Variable(String name, Object value, int scopeDepth) {
        this.name = name;
        this.value = value;
        this.scopeDepth = scopeDepth;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public int getScopeDepth() {
        return scopeDepth;
    }
}
