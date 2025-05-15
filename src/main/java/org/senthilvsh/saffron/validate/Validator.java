package org.senthilvsh.saffron.validate;

import org.senthilvsh.saffron.ast.*;
import org.senthilvsh.saffron.runtime.Type;

public class Validator {
    public void validate(Program program) throws ValidatorException {
        for (Statement s : program.getStatements()) {
            if (s instanceof ExpressionStatement es) {
                getType(es.getExpression());
            }
        }
    }

    public Type getType(Expression expression) throws ValidatorException {
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
                if ("*".equals(operator)) {
                    return multiply(left, right);
                }
                if ("/".equals(operator)) {
                    return divide(left, right);
                }
                if ("%".equals(operator)) {
                    return modulo(left, right);
                }
                if (">".equals(operator)) {
                    return greaterThan(left, right);
                }
                if (">=".equals(operator)) {
                    return greaterThanOrEqual(left, right);
                }
                if ("<".equals(operator)) {
                    return lessThan(left, right);
                }
                if ("<=".equals(operator)) {
                    return lessThanOrEqual(left, right);
                }
            } catch (RuntimeException ex) {
                throw new ValidatorException(String.format("Cannot perform '%s' operation between %s and %s",
                        operator, left.getName(), right.getName()), binaryExpression.getOperatorPosition(), binaryExpression.getOperatorLength());
            }
        }
        throw new ValidatorException("Unknown expression type", expression.getPosition(), expression.getLength());
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

    Type multiply(Type left, Type right) {
        if (left == Type.NUMBER && right == Type.NUMBER) {
            return Type.NUMBER;
        }
        throw new RuntimeException();
    }

    Type divide(Type left, Type right) {
        if (left == Type.NUMBER && right == Type.NUMBER) {
            return Type.NUMBER;
        }
        throw new RuntimeException();
    }

    Type modulo(Type left, Type right) {
        if (left == Type.NUMBER && right == Type.NUMBER) {
            return Type.NUMBER;
        }
        throw new RuntimeException();
    }

    Type greaterThan(Type left, Type right) {
        if (left == Type.NUMBER && right == Type.NUMBER) {
            return Type.NUMBER;
        }
        throw new RuntimeException();
    }

    Type greaterThanOrEqual(Type left, Type right) {
        if (left == Type.NUMBER && right == Type.NUMBER) {
            return Type.NUMBER;
        }
        throw new RuntimeException();
    }

    Type lessThan(Type left, Type right) {
        if (left == Type.NUMBER && right == Type.NUMBER) {
            return Type.NUMBER;
        }
        throw new RuntimeException();
    }

    Type lessThanOrEqual(Type left, Type right) {
        if (left == Type.NUMBER && right == Type.NUMBER) {
            return Type.NUMBER;
        }
        throw new RuntimeException();
    }
}
