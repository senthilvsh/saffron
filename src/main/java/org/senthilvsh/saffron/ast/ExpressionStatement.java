package org.senthilvsh.saffron.ast;

public class ExpressionStatement extends Statement {
    private final Expression expression;

    public ExpressionStatement(Expression expression, int position, int length) {
        super(position, length);
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }
}
