package org.senthilvsh.saffron.validate;

import org.senthilvsh.saffron.ast.*;
import org.senthilvsh.saffron.common.Type;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Validator {
    // TODO: Create classes to encapsulate type-checking information
    private final Map<String, Type> variableTypes = new HashMap<>();
    private final Map<String, Boolean> variableAssignmentStatus = new HashMap<>();

    public void validate(Program program) throws ValidationError {
        for (Statement s : program.getStatements()) {
            validate(s);
        }
    }

    private void validate(Statement statement) throws ValidationError {
        if (statement instanceof ExpressionStatement es) {
            getType(es.getExpression());
        } else if (statement instanceof BlockStatement bs) {
            List<Statement> statements = bs.getStatements();
            for (Statement s : statements) {
                validate(s);
            }
        } else if (statement instanceof VariableDeclaration vds) {
            String name = vds.getName();
            if (variableTypes.containsKey(name)) {
                throw new ValidationError(String.format("Re-declaration of variable '%s'", name), vds.getPosition(), vds.getLength());
            }
            variableTypes.put(name, Type.of(vds.getType()));
        }
    }

    public Type getType(Expression expression) throws ValidationError {
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
                throw new ValidationError(String.format("Undeclared variable '%s'", identifier.getName()),
                        identifier.getPosition(), identifier.getLength());
            }
            if (!variableAssignmentStatus.containsKey(identifier.getName())) {
                throw new ValidationError(String.format("Variable '%s' is used before being assigned", identifier.getName()),
                        identifier.getPosition(), identifier.getLength());
            }
            return variableTypes.get(identifier.getName());
        }
        if (expression instanceof UnaryExpression unaryExpression) {
            String operator = unaryExpression.getOperator();
            Expression operand = unaryExpression.getOperand();
            Type operandType = getType(operand);
            if ("+-".contains(operator)) {
                if (operandType != Type.NUMBER) {
                    throw new ValidationError(
                            String.format("Operation '%s' cannot be applied to '%s'", operator, operandType.getName()),
                            unaryExpression.getOperatorPosition(),
                            unaryExpression.getOperatorLength()
                    );
                }
                return Type.NUMBER;
            }
        }
        if (expression instanceof BinaryExpression binaryExpression) {
            String operator = binaryExpression.getOperator();

            if ("=".equals(operator)) {
                return assign(binaryExpression);
            }

            Type left = getType(binaryExpression.getLeft());
            Type right = getType(binaryExpression.getRight());
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
            } catch (RuntimeException ex) {
                throw new ValidationError(String.format("Cannot perform '%s' operation between %s and %s",
                        operator, left.getName(), right.getName()), binaryExpression.getOperatorPosition(), binaryExpression.getOperatorLength());
            }
        }
        throw new ValidationError("Unknown expression type", expression.getPosition(), expression.getLength());
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

    Type assign(BinaryExpression binaryExpression) throws ValidationError {
        if (!(binaryExpression.getLeft() instanceof Identifier identifier)) {
            throw new ValidationError("Left side of assignment must be a variable",
                    binaryExpression.getLeft().getPosition(), binaryExpression.getLeft().getLength());
        }
        if (!variableTypes.containsKey(identifier.getName())) {
            throw new ValidationError(String.format("Undeclared variable '%s'", identifier.getName()),
                    identifier.getPosition(), identifier.getLength());
        }
        Type variableType = variableTypes.get(identifier.getName());
        Type right = getType(binaryExpression.getRight());
        if (variableType == right) {
            variableAssignmentStatus.put(identifier.getName(), true);
            return right;
        }
        throw new ValidationError(String.format("Cannot assign value of type '%s' to variable of type '%s'",
                right.getName(), variableType.getName()),
                binaryExpression.getOperatorPosition(), binaryExpression.getOperatorLength());
    }
}
