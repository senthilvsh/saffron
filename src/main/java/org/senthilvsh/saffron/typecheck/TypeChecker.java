package org.senthilvsh.saffron.typecheck;

import org.senthilvsh.saffron.ast.*;
import org.senthilvsh.saffron.runtime.Type;

public class TypeChecker {
    public void check(Program program) throws TypeCheckerException {
        for (Statement s : program.getStatements()) {
            if (s instanceof ExpressionStatement es) {
                getType(es.getExpression());
            }
        }
    }

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
            try {
                if ("+".equals(operator)) {
                    return add(left, right);
                }
                if ("-".equals(operator)) {
                    return subtract(left, right);

                }
            } catch (RuntimeException ex) {
                throw new TypeCheckerException(String.format("Cannot perform '%s' operation between %s and %s",
                        operator, left.getName(), right.getName()), binaryExpression.getOperatorPosition(), binaryExpression.getOperatorLength());
            }
        }
        throw new TypeCheckerException("Unknown expression type", expression.getPosition(), expression.getLength());
    }

    Type add(Type left, Type right) {
        if (left == Type.NUMBER && right == Type.NUMBER) {
            return Type.NUMBER;
        }
        if (left == Type.STRING || right == Type.STRING) {
            return Type.STRING;
        }
        throw new RuntimeException();
    }

    Type subtract(Type left, Type right) {
        if (left == Type.NUMBER && right == Type.NUMBER) {
            return Type.NUMBER;
        }
        throw new RuntimeException();
    }
}
