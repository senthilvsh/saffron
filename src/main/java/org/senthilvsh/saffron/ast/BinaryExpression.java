package org.senthilvsh.saffron.ast;

public class BinaryExpression extends Expression {
    private final Expression left;
    private final String operator;
    private final Expression right;
    private final int operatorPosition;
    private final int operatorLength;

    public BinaryExpression(Expression left, String operator, Expression right, int position, int length, int operatorPosition, int operatorLength) {
        super(position, length);
        this.left = left;
        this.operator = operator;
        this.right = right;
        this.operatorPosition = operatorPosition;
        this.operatorLength = operatorLength;
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

    public int getOperatorPosition() {
        return operatorPosition;
    }

    public int getOperatorLength() {
        return operatorLength;
    }
}
