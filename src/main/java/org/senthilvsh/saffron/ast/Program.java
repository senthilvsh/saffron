package org.senthilvsh.saffron.ast;

public class Program {
    private final Expression expression;

    public Program(Expression expression) {
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }
}
