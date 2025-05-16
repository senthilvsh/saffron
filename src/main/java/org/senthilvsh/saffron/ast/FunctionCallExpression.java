package org.senthilvsh.saffron.ast;

import java.util.List;

public class FunctionCallExpression extends Expression {
    private final String name;
    private final List<Expression> arguments;

    public FunctionCallExpression(String name, List<Expression> arguments, int position, int length) {
        super(position, length);
        this.name = name;
        this.arguments = arguments;
    }

    public String getName() {
        return name;
    }

    public List<Expression> getArguments() {
        return arguments;
    }
}
