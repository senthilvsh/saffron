package org.senthilvsh.saffron.typecheck;

import org.senthilvsh.saffron.ast.*;
import org.senthilvsh.saffron.runtime.Type;

public class TypeChecker {
    public Type getType(Expression expression) throws TypeCheckerException {
        if (expression instanceof NumberLiteral) {
            return Type.NUMBER;
        }
        if (expression instanceof StringLiteral) {
            return Type.STRING;
        }
        if (expression instanceof BooleanLiteral) {
            return Type.BOOLEAN;
        }
        if (expression instanceof BinaryExpression binaryExpression) {
            Type left = getType(binaryExpression.getLeft());
            Type right = getType(binaryExpression.getRight());
            String operator = binaryExpression.getOperator();
            if ("+".equals(operator) || "-".equals(operator)) {
                if (left == Type.NUMBER && right == Type.NUMBER) {
                    return Type.NUMBER;
                }
                if (left == Type.STRING || right == Type.STRING) {
                    return Type.STRING;
                }
                // TODO: Report the position of operator only. It makes sense here.
                throw new TypeCheckerException(String.format("Cannot perform '%s' operation between %s and %s",
                        operator, left.getName(), right.getName()), binaryExpression.getPosition(), binaryExpression.getLength());
            }
        }
        throw new TypeCheckerException("Unknown expression type", expression.getPosition(), expression.getLength());
    }
}
