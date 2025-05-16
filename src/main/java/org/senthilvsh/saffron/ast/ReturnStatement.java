package org.senthilvsh.saffron.ast;

public class ReturnStatement extends Statement {
    private final Expression expression;

    public ReturnStatement(Expression expression, int position, int length) {
        super(position, length);
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }
}
