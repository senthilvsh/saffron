package org.senthilvsh.saffron.ast;

public class PrintStatement extends Statement {
    private final Expression expression;

    public PrintStatement(Expression expression, int position, int length) {
        super(position, length);
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }
}
