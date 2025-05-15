package org.senthilvsh.saffron.runtime;

import org.senthilvsh.saffron.ast.*;
import org.senthilvsh.saffron.common.Type;

import java.util.HashMap;
import java.util.Map;

public class Interpreter {
    private final Map<String, Variable> variables = new HashMap<>();

    public void execute(Program program) throws InterpreterException {
        for (Statement s : program.getStatements()) {
            execute(s);
        }
    }

    private void execute(Statement statement) throws InterpreterException {
        if (statement instanceof ExpressionStatement es) {
            evaluate(es.getExpression());
        } else if (statement instanceof PrintStatement ps) {
            BaseObj result = evaluate(ps.getExpression());
            System.out.println(stringValue(result));
        } else if (statement instanceof VariableDeclarationStatement vds) {
            String name = vds.getName();
            if (variables.containsKey(name)) {
                throw new InterpreterException(String.format("Re-declaration of variable '%s'", name),
                        vds.getPosition(), vds.getLength());
            }
            Variable v = new Variable(name, Type.of(vds.getType()), null);
            variables.put(name, v);
        }
    }

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
        if (expression instanceof Identifier i) {
            if (!variables.containsKey(i.getName())) {
                throw new InterpreterException(String.format("Undefined variable '%s'", i.getName()),
                        i.getPosition(), i.getLength());
            }
            Variable variable = variables.get(i.getName());
            if (variable.getValue() == null) {
                throw new InterpreterException(String.format("Variable '%s' is used before being assigned", i.getName()),
                        i.getPosition(), i.getLength());
            }
            return variable.getValue();
        }
        if (expression instanceof BinaryExpression binaryExpression) {
            String operator = binaryExpression.getOperator();

            if ("=".equals(operator)) {
                return assign(binaryExpression);
            }

            BaseObj left = evaluate(binaryExpression.getLeft());
            BaseObj right = evaluate(binaryExpression.getRight());

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
                throw new InterpreterException(String.format("Cannot perform operation '%s' on %s and %s", operator,
                        left.getType().getName(), right.getType().getName()), binaryExpression.getOperatorPosition(), binaryExpression.getOperatorLength());
            }
        }
        throw new InterpreterException("Unknown expression type", expression.getPosition(), expression.getLength());
    }

    BaseObj add(BaseObj left, BaseObj right) {
        if (left.getType() == Type.NUMBER && right.getType() == Type.NUMBER) {
            return new NumberObj(((NumberObj) left).getValue() + ((NumberObj) right).getValue());
        }
        if (left.getType() == Type.STRING || right.getType() == Type.STRING) {
            String leftStr = stringValue(left);
            String rightStr = stringValue(right);
            return new StringObj(leftStr + rightStr);
        }
        throw new RuntimeException();
    }

    BaseObj subtract(BaseObj left, BaseObj right) {
        if (left.getType() == Type.NUMBER && right.getType() == Type.NUMBER) {
            return new NumberObj(((NumberObj) left).getValue() - ((NumberObj) right).getValue());
        }
        throw new RuntimeException();
    }

    BaseObj multiply(BaseObj left, BaseObj right) {
        if (left.getType() == Type.NUMBER && right.getType() == Type.NUMBER) {
            return new NumberObj(((NumberObj) left).getValue() * ((NumberObj) right).getValue());
        }
        throw new RuntimeException();
    }

    BaseObj divide(BaseObj left, BaseObj right) {
        if (left.getType() == Type.NUMBER && right.getType() == Type.NUMBER) {
            return new NumberObj(((NumberObj) left).getValue() / ((NumberObj) right).getValue());
        }
        throw new RuntimeException();
    }

    BaseObj modulo(BaseObj left, BaseObj right) {
        if (left.getType() == Type.NUMBER && right.getType() == Type.NUMBER) {
            return new NumberObj(((NumberObj) left).getValue() % ((NumberObj) right).getValue());
        }
        throw new RuntimeException();
    }

    BaseObj greaterThan(BaseObj left, BaseObj right) {
        if (left.getType() == Type.NUMBER && right.getType() == Type.NUMBER) {
            return new BooleanObj(((NumberObj) left).getValue() > ((NumberObj) right).getValue());
        }
        throw new RuntimeException();
    }

    BaseObj greaterThanOrEqual(BaseObj left, BaseObj right) {
        if (left.getType() == Type.NUMBER && right.getType() == Type.NUMBER) {
            return new BooleanObj(((NumberObj) left).getValue() >= ((NumberObj) right).getValue());
        }
        throw new RuntimeException();
    }

    BaseObj lessThan(BaseObj left, BaseObj right) {
        if (left.getType() == Type.NUMBER && right.getType() == Type.NUMBER) {
            return new BooleanObj(((NumberObj) left).getValue() < ((NumberObj) right).getValue());
        }
        throw new RuntimeException();
    }

    BaseObj lessThanOrEqual(BaseObj left, BaseObj right) {
        if (left.getType() == Type.NUMBER && right.getType() == Type.NUMBER) {
            return new BooleanObj(((NumberObj) left).getValue() <= ((NumberObj) right).getValue());
        }
        throw new RuntimeException();
    }

    BaseObj equal(BaseObj left, BaseObj right) {
        if (left.getType() == right.getType()) {
            if (left.getType() == Type.NUMBER) {
                return new BooleanObj(((NumberObj) left).getValue() == ((NumberObj) right).getValue());
            } else if (left.getType() == Type.STRING) {
                return new BooleanObj(((StringObj) left).getValue().equals(((StringObj) right).getValue()));
            } else if (left.getType() == Type.BOOLEAN) {
                return new BooleanObj(((BooleanObj) left).getValue() == ((BooleanObj) right).getValue());
            } else {
                // Not a type that supports == operator
                throw new RuntimeException();
            }
        }
        // Types of left and right are different
        throw new RuntimeException();
    }

    BaseObj notEqual(BaseObj left, BaseObj right) {
        if (left.getType() == right.getType()) {
            if (left.getType() == Type.NUMBER) {
                return new BooleanObj(((NumberObj) left).getValue() != ((NumberObj) right).getValue());
            } else if (left.getType() == Type.STRING) {
                return new BooleanObj(!((StringObj) left).getValue().equals(((StringObj) right).getValue()));
            } else if (left.getType() == Type.BOOLEAN) {
                return new BooleanObj(((BooleanObj) left).getValue() != ((BooleanObj) right).getValue());
            } else {
                // Not a type that supports != operator
                throw new RuntimeException();
            }
        }
        // Types of left and right are different
        throw new RuntimeException();
    }

    BaseObj assign(BinaryExpression binaryExpression) throws InterpreterException {
        String variableName = getVariableName(binaryExpression);

        BaseObj right = evaluate(binaryExpression.getRight());

        Variable variable = variables.get(variableName);
        if (variable.getType() != right.getType()) {
            throw new InterpreterException(String.format("Cannot assign value of type '%s' to variable of type '%s'",
                    right.getType(), variable.getType()),
                    binaryExpression.getOperatorPosition(), binaryExpression.getOperatorLength());
        }

        BaseObj newObj;

        if (right instanceof NumberObj n) {
            newObj = new NumberObj(n.getValue());
        } else if (right instanceof StringObj s) {
            newObj = new StringObj(s.getValue());
        } else if (right instanceof BooleanObj b) {
            newObj = new BooleanObj(b.getValue());
        } else {
            newObj = right;
        }

        variable.setValue(newObj);

        return newObj;
    }

    private String getVariableName(BinaryExpression binaryExpression) throws InterpreterException {
        Expression left = binaryExpression.getLeft();

        if (!(left instanceof Identifier identifier)) {
            throw new InterpreterException("Left side of assignment must be a variable", left.getPosition(), left.getLength());
        }

        String variableName = identifier.getName();
        if (!variables.containsKey(variableName)) {
            throw new InterpreterException(String.format("Undeclared variable '%s'", identifier.getName()),
                    left.getPosition(), left.getLength());
        }
        return variableName;
    }

    private String stringValue(BaseObj obj) {
        if (obj instanceof NumberObj numberObj) {
            double val = numberObj.getValue();
            if (val == (long) val) {
                return String.format("%d", (long) numberObj.getValue());
            } else {
                return String.valueOf(numberObj.getValue());
            }
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
