package org.senthilvsh.saffron.runtime;

import org.senthilvsh.saffron.ast.*;

public class Interpreter {
    public void execute(Program program) throws InterpreterException {
        for (Statement s : program.getStatements()) {
            if (s instanceof ExpressionStatement es) {
                BaseObj result = evaluate(es.getExpression());
                System.out.println(stringValue(result));
            }
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
        if (expression instanceof BinaryExpression binaryExpression) {
            BaseObj left = evaluate(binaryExpression.getLeft());
            BaseObj right = evaluate(binaryExpression.getRight());
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
