package org.senthilvsh.saffron.ast;

public class BinaryExpression extends Expression {
    private final Expression left;
    private final String operator;
    private final Expression right;

    public BinaryExpression(Expression left, String operator, Expression right, int position, int length) {
        super(position, length);
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    public Expression getLeft() {
        return left;
    }

    public String getOperator() {
        return operator;
    }

    public Expression getRight() {
        return right;
    }
}
