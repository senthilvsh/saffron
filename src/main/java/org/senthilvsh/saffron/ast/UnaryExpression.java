package org.senthilvsh.saffron.ast;

public class UnaryExpression extends Expression {
    private final String operator;
    private final Expression operand;
    private final int operatorPosition;
    private final int operatorLength;

    public UnaryExpression(String operator, Expression operand, int position, int length, int operatorPosition, int operatorLength) {
        super(position, length);
        this.operator = operator;
        this.operand = operand;
        this.operatorPosition = operatorPosition;
        this.operatorLength = operatorLength;
    }

    public String getOperator() {
        return operator;
    }

    public Expression getOperand() {
        return operand;
    }

    public int getOperatorPosition() {
        return operatorPosition;
    }

    public int getOperatorLength() {
        return operatorLength;
    }
}
