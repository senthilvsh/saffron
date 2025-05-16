package org.senthilvsh.saffron.ast;

import org.senthilvsh.saffron.common.Type;

public class FunctionArgument {
    private final String name;
    private final Type type;

    public FunctionArgument(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }
}
