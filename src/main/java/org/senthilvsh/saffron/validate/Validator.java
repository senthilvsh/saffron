package org.senthilvsh.saffron.validate;

import org.senthilvsh.saffron.ast.*;
import org.senthilvsh.saffron.common.Type;

import java.util.HashMap;
import java.util.Map;

public class Validator {
    private final Map<String, Type> variableTypes = new HashMap<>();

    public void validate(Program program) throws ValidatorException {
        for (Statement s : program.getStatements()) {
            validate(s);
        }
    }

    private void validate(Statement statement) throws ValidatorException {
        if (statement instanceof ExpressionStatement es) {
            getType(es.getExpression());
        } else if (statement instanceof VariableDeclarationStatement vds) {
            String name = vds.getName();
            if (variableTypes.containsKey(name)) {
                throw new ValidatorException(String.format("Re-declaration of variable '%s'", name), vds.getPosition(), vds.getLength());
            }
            variableTypes.put(name, Type.of(vds.getType()));
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
        if (expression instanceof Identifier identifier) {
            if (!variableTypes.containsKey(identifier.getName())) {
                throw new ValidatorException(String.format("Undeclared variable '%s'", identifier.getName()),
                        identifier.getPosition(), identifier.getLength());
            }
            return variableTypes.get(identifier.getName());
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
                if ("==".equals(operator)) {
                    return equal(left, right);
                }
                if ("!=".equals(operator)) {
                    return notEqual(left, right);
                }
                if ("=".equals(operator)) {
                    return assign(left, right);
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

    /**
     * Equals operator can be applied only between objects of the same type AND the type must be
     * a number, string or boolean.
     */
    Type equal(Type left, Type right) {
        if (left == right && (left == Type.NUMBER || left == Type.BOOLEAN || left == Type.STRING)) {
            return Type.BOOLEAN;
        }
        throw new RuntimeException();
    }

    /**
     * Not Equals operator can be applied only between objects of the same type AND the type must be
     * a number, string or boolean.
     */
    Type notEqual(Type left, Type right) {
        if (left == right && (left == Type.NUMBER || left == Type.BOOLEAN || left == Type.STRING)) {
            return Type.BOOLEAN;
        }
        throw new RuntimeException();
    }

    Type assign(Type left, Type right) {
        if (left == right) {
            return left;
        }
        throw new RuntimeException();
    }
}
