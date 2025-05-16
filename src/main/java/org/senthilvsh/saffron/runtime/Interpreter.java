package org.senthilvsh.saffron.runtime;

import org.senthilvsh.saffron.ast.*;
import org.senthilvsh.saffron.common.Frame;
import org.senthilvsh.saffron.common.FrameStack;
import org.senthilvsh.saffron.common.Type;
import org.senthilvsh.saffron.common.Variable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Interpreter {
    private final FrameStack stack = new FrameStack();
    private final Map<String, FunctionDefinition> functions = new HashMap<>();

    private FunctionDefinition functionUnderEvaluation = null;

    public Interpreter() {
        stack.push(new Frame());
    }

    public void execute(Program program) throws RuntimeError {
        for (Statement s : program.getStatements()) {
            execute(s);
        }
    }

    private void execute(Statement statement) throws RuntimeError {
        if (statement instanceof ExpressionStatement es) {
            evaluate(es.getExpression());
        } else if (statement instanceof BlockStatement bs) {
            List<Statement> statements = bs.getStatements();
            for (Statement s : statements) {
                execute(s);
            }
        } else if (statement instanceof PrintStatement ps) {
            BaseObj result = evaluate(ps.getExpression());
            System.out.println(stringValue(result));
        } else if (statement instanceof ReturnStatement rs) {
            Expression expression = rs.getExpression();
            BaseObj returnValue = null;
            if (expression != null) {
                returnValue = evaluate(expression);
            }
            // TODO: Check whether computed return value matches the function definition's return value
            throw new FunctionReturn(returnValue);
        } else if (statement instanceof ConditionalStatement cs) {
            Expression condition = cs.getCondition();
            BaseObj baseObj = evaluate(condition);
            if (baseObj.getType() != Type.BOOLEAN) {
                throw new RuntimeError("The condition of an 'if' statement must be a boolean expression",
                        condition.getPosition(), condition.getLength());
            }
            BooleanObj conditionResult = (BooleanObj) baseObj;
            if (conditionResult.getValue()) {
                execute(cs.getTrueClause());
            } else {
                if (cs.getFalseClause() != null) {
                    execute(cs.getFalseClause());
                }
            }
        } else if (statement instanceof WhileLoop wl) {
            Expression condition = wl.getCondition();
            BaseObj baseObj = evaluate(condition);
            if (baseObj.getType() != Type.BOOLEAN) {
                throw new RuntimeError("The condition of a 'while' loop must be a boolean expression",
                        condition.getPosition(), condition.getLength());
            }
            BooleanObj conditionResult = (BooleanObj) baseObj;
            try {
                while (conditionResult.getValue()) {
                    try {
                        execute(wl.getBody());
                    } catch (ContinueLoop e) {
                        // Next iteration
                    }
                    conditionResult = (BooleanObj) evaluate(condition);
                }
            } catch (BreakLoop e) {
                // Break out of loop
            }
        } else if (statement instanceof BreakStatement) {
            throw new BreakLoop();
        } else if (statement instanceof VariableDeclaration vds) {
            String name = vds.getName();
            Frame frame = stack.peek();
            if (frame.containsKey(name)) {
                throw new RuntimeError(String.format("Re-declaration of variable '%s'", name),
                        vds.getPosition(), vds.getLength());
            }
            if (Type.of(vds.getType()) == Type.VOID) {
                throw new RuntimeError(String.format("Variables cannot have '%s' type", Type.VOID.getName()),
                        vds.getPosition(), vds.getLength());
            }
            Variable v = new Variable(name, Type.of(vds.getType()), null, true);
            frame.put(name, v);
        } else if (statement instanceof FunctionDefinition fd) {
            String signature = fd.getSignature();
            if (functions.containsKey(signature)) {
                throw new RuntimeError(
                        String.format("Function re-declaration: %s(%s)",
                                fd.getName(),
                                fd.getArguments()
                                        .stream()
                                        .map(a -> a.getType().getName().toLowerCase())
                                        .collect(Collectors.joining(","))),
                        fd.getNamePosition(),
                        fd.getNameLength());
            }
            functions.put(signature, fd);
        }
    }

    public BaseObj evaluate(Expression expression) throws RuntimeError {
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
            Frame frame = stack.peek();
            if (!frame.containsKey(i.getName())) {
                throw new RuntimeError(String.format("Undefined variable '%s'", i.getName()),
                        i.getPosition(), i.getLength());
            }
            Variable variable = frame.get(i.getName());
            if (variable.getValue() == null) {
                throw new RuntimeError(String.format("Variable '%s' is used before being assigned", i.getName()),
                        i.getPosition(), i.getLength());
            }
            return variable.getValue();
        }
        if (expression instanceof FunctionCallExpression call) {
            String name = call.getName();
            String signature = name;
            List<BaseObj> args = new ArrayList<>();
            for (Expression e : call.getArguments()) {
                args.add(evaluate(e));
            }
            List<String> argTypes = args.stream().map(a -> a.getType().getName().toLowerCase()).toList();
            if (!argTypes.isEmpty()) {
                signature += "_" + String.join("_", argTypes);
            }
            if (!functions.containsKey(signature)) {
                throw new RuntimeError(
                        String.format("Undeclared function %s(%s)", name, String.join(",", argTypes)),
                        call.getPosition(), call.getLength());
            }
            FunctionDefinition fd = functions.get(signature);
            try {
                stack.newFrame();
                Frame frame = stack.peek();
                var arguments = fd.getArguments();
                for (int i = 0; i < arguments.size(); i++) {
                    frame.put(arguments.get(i).getName(), new Variable(arguments.get(i).getName(), arguments.get(i).getType(), args.get(i), true));
                }
                functionUnderEvaluation = fd;

                execute(functions.get(signature).getBody());

                functionUnderEvaluation = null;
                stack.pop();
                return null;
            } catch (FunctionReturn e) {
                functionUnderEvaluation = null;
                stack.pop();
                return e.getReturnValue();
            }
        }
        if (expression instanceof UnaryExpression unaryExpression) {
            String operator = unaryExpression.getOperator();
            Expression operand = unaryExpression.getOperand();
            BaseObj baseObj = evaluate(operand);
            if ("+-".contains(operator)) {
                if (baseObj.getType() != Type.NUMBER) {
                    throw new RuntimeError(
                            String.format("Operation '%s' cannot be applied to '%s'", operator, baseObj.getType().getName()),
                            unaryExpression.getOperatorPosition(),
                            unaryExpression.getOperatorLength()
                    );
                }
                if ("+".equals(operator)) {
                    return baseObj;
                }
                if ("-".equals(operator)) {
                    NumberObj numberObj = (NumberObj) baseObj;
                    return new NumberObj(-1 * numberObj.getValue());
                }
            }
            if ("!".contains(operator)) {
                if (baseObj.getType() != Type.BOOLEAN) {
                    throw new RuntimeError(
                            String.format("Operation '%s' cannot be applied to '%s'", operator, baseObj.getType().getName()),
                            unaryExpression.getOperatorPosition(),
                            unaryExpression.getOperatorLength()
                    );
                }
                BooleanObj booleanObj = (BooleanObj) baseObj;
                return new BooleanObj(!booleanObj.getValue());
            }
            throw new RuntimeError(
                    String.format("Invalid unary operator '%s'", operator),
                    unaryExpression.getOperatorPosition(),
                    unaryExpression.getOperatorLength()
            );
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
                if ("&&".equals(operator)) {
                    return logicalAnd(left, right);
                }
                if ("||".equals(operator)) {
                    return logicalOr(left, right);
                }
            } catch (RuntimeException ex) {
                throw new RuntimeError(String.format("Cannot perform operation '%s' on %s and %s", operator,
                        left.getType().getName(), right.getType().getName()), binaryExpression.getOperatorPosition(), binaryExpression.getOperatorLength());
            }
        }
        throw new RuntimeError("Unknown expression type", expression.getPosition(), expression.getLength());
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

    BaseObj logicalAnd(BaseObj left, BaseObj right) {
        if (left.getType() == Type.BOOLEAN && right.getType() == Type.BOOLEAN) {
            BooleanObj leftBoolean = (BooleanObj) left;
            BooleanObj rightBoolean = (BooleanObj) right;
            return new BooleanObj(leftBoolean.getValue() && rightBoolean.getValue());
        }
        throw new RuntimeException();
    }

    BaseObj logicalOr(BaseObj left, BaseObj right) {
        if (left.getType() == Type.BOOLEAN && right.getType() == Type.BOOLEAN) {
            BooleanObj leftBoolean = (BooleanObj) left;
            BooleanObj rightBoolean = (BooleanObj) right;
            return new BooleanObj(leftBoolean.getValue() || rightBoolean.getValue());
        }
        throw new RuntimeException();
    }

    BaseObj assign(BinaryExpression binaryExpression) throws RuntimeError {
        String variableName = getVariableName(binaryExpression);

        BaseObj right = evaluate(binaryExpression.getRight());

        Frame frame = stack.peek();
        Variable variable = frame.get(variableName);
        if (variable.getType() != right.getType()) {
            throw new RuntimeError(String.format("Cannot assign value of type '%s' to variable of type '%s'",
                    right.getType().getName(), variable.getType().getName()),
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

    private String getVariableName(BinaryExpression binaryExpression) throws RuntimeError {
        Expression left = binaryExpression.getLeft();

        if (!(left instanceof Identifier identifier)) {
            throw new RuntimeError("Left side of assignment must be a variable", left.getPosition(), left.getLength());
        }

        Frame frame = stack.peek();
        String variableName = identifier.getName();
        if (!frame.containsKey(variableName)) {
            throw new RuntimeError(String.format("Undeclared variable '%s'", identifier.getName()),
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
