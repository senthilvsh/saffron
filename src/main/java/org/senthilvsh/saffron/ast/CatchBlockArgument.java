package org.senthilvsh.saffron.ast;

import org.senthilvsh.saffron.common.Type;

public class CatchBlockArgument extends Expression {
    private final String name;
    private final Type type;

    public CatchBlockArgument(String name, Type type, int position, int length) {
        super(position, length);
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
