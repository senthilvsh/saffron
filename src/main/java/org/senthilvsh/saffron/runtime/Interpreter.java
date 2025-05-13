package org.senthilvsh.saffron.runtime;

import org.senthilvsh.saffron.ast.*;

public class Interpreter {
    public BaseObj evaluate(Expression expression) throws InterpreterException {
        if (expression instanceof NumberLiteral n) {
            return new NumberObj(n.getValue());
        }
        if (expression instanceof StringLiteral s) {
            return new StringObj(s.getValue());
        }
        if (expression instanceof BooleanLiteral b) {
            return new BooleanObj(b.getValue());
        }
        if (expression instanceof BinaryExpression binaryExpression) {
            BaseObj left = evaluate(binaryExpression.getLeft());
            BaseObj right = evaluate(binaryExpression.getRight());
            String operator = binaryExpression.getOperator();
            if ("+".equals(operator) || "-".equals(operator)) {
                if (left.getType() == Type.NUMBER && right.getType() == Type.NUMBER) {
                    return new NumberObj(((NumberObj) left).getValue() + ((NumberObj) right).getValue());
                }
                if (left.getType() == Type.STRING || right.getType() == Type.STRING) {
                    String leftStr = stringValue(left);
                    String rightStr = stringValue(right);
                    return new StringObj(leftStr + rightStr);
                }
                // TODO: Report the position of operator only. It makes sense here.
                throw new InterpreterException(String.format("Cannot perform '%s' operation between %s and %s",
                        operator, left.getType().getName(), right.getType().getName()), binaryExpression.getPosition(), binaryExpression.getLength());
            }
        }
        throw new InterpreterException("Unknown expression type", expression.getPosition(), expression.getLength());
    }

    private String stringValue(BaseObj obj) {
        if (obj instanceof NumberObj numberObj) {
            return String.valueOf(numberObj.getValue());
        }
        if (obj instanceof StringObj stringObj) {
            return stringObj.getValue();
        }
        if (obj instanceof BooleanObj booleanObj) {
            return String.valueOf(booleanObj.getValue());
        }
        return obj.toString();
    }
}
