package org.senthilvsh.saffron.ast;

// TODO: Rename 'print' to 'write'.
// TODO: Implement a 'read' statement.
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
