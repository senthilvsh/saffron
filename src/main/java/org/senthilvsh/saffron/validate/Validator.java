package org.senthilvsh.saffron.validate;

import org.senthilvsh.saffron.ast.*;
import org.senthilvsh.saffron.common.Frame;
import org.senthilvsh.saffron.common.FrameStack;
import org.senthilvsh.saffron.common.Type;
import org.senthilvsh.saffron.common.Variable;
import org.senthilvsh.saffron.runtime.StringObj;
import org.senthilvsh.saffron.stdlib.NativeFunctionsRegistry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.senthilvsh.saffron.common.Type.STRING;
import static org.senthilvsh.saffron.common.Type.VOID;

public class Validator {
    private final FrameStack stack = new FrameStack();
    private final Map<String, FunctionDefinition> functions = new HashMap<>();

    private FunctionDefinition functionUnderEvaluation = null;

    public Validator() {
        functions.putAll(NativeFunctionsRegistry.getAll());
        stack.push(new Frame());
    }

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
        } else if (statement instanceof ReturnStatement rs) {
            Expression expression = rs.getExpression();
            Type type = VOID;
            if (expression != null) {
                type = getType(expression);
            }
            if (functionUnderEvaluation != null) {
                Type returnType = functionUnderEvaluation.getReturnType();
                if (type != returnType) {
                    throw new ValidationError(
                            String.format("Must return a value of type '%s'", returnType.getName()),
                            rs.getPosition(),
                            rs.getLength());
                }
            }
        } else if (statement instanceof ConditionalStatement cs) {
            Expression condition = cs.getCondition();
            Type conditionType = getType(condition);
            if (conditionType != Type.BOOLEAN) {
                throw new ValidationError("The condition of an 'if' statement must be a boolean expression",
                        condition.getPosition(), condition.getLength());
            }
            stack.newBlockScope();
            validate(cs.getTrueClause());
            stack.pop();
            if (cs.getFalseClause() != null) {
                stack.newBlockScope();
                validate(cs.getFalseClause());
                stack.pop();
            }
        } else if (statement instanceof WhileLoop wl) {
            Expression condition = wl.getCondition();
            Type conditionType = getType(condition);
            if (conditionType != Type.BOOLEAN) {
                throw new ValidationError("The condition of a 'while' loop must be a boolean expression",
                        condition.getPosition(), condition.getLength());
            }
            stack.newBlockScope();
            validate(wl.getBody());
            stack.pop();
        } else if (statement instanceof TryCatchStatement tcs) {
            stack.newBlockScope();
            validate(tcs.getTryBlock());
            stack.pop();
            CatchBlockArgument exType = tcs.getExceptionType();
            if (exType.getType() != STRING) {
                throw new ValidationError("The type of the first argument must be string", exType.getPosition(), exType.getLength());
            }
            CatchBlockArgument exMsg = tcs.getExceptionMessage();
            if (exMsg.getType() != STRING) {
                throw new ValidationError("The type of the second argument must be string", exMsg.getPosition(), exMsg.getLength());
            }
            stack.newBlockScope();
            String typeArgName = tcs.getExceptionType().getName();
            Variable typeArgVariable = new Variable(
                    typeArgName,
                    Type.STRING,
                    null,
                    true
            );

            String msgArgName = tcs.getExceptionMessage().getName();
            Variable msgArgVariable = new Variable(
                    msgArgName,
                    Type.STRING,
                    null,
                    true
            );

            stack.peek().put(typeArgName, typeArgVariable);
            stack.peek().put(msgArgName, msgArgVariable);
            validate(tcs.getCatchBlock());
            stack.pop();
        } else if (statement instanceof VariableDeclaration vds) {
            String name = vds.getName();
            Type variableType = Type.of(vds.getType());
            if (variableType == VOID) {
                throw new ValidationError(String.format("Variables cannot have '%s' type", VOID.getName()),
                        vds.getPosition(), vds.getLength());
            }
            Frame frame = stack.peek();
            if (frame.containsKey(name) && frame.get(name).isCurrentScope()) {
                throw new ValidationError(String.format("Re-declaration of variable '%s'", name), vds.getPosition(), vds.getLength());
            }

            if (vds.getExpression() != null) {
                Type expType = getType(vds.getExpression());
                if (expType != variableType) {
                    throw new ValidationError(
                            String.format("Value of type '%s' cannot be assigned to variable of type '%s'",
                                    expType.getName(), variableType.getName()),
                            vds.getExpression().getPosition(), vds.getExpression().getLength()
                    );
                }
            }

            frame.put(name, new Variable(name, Type.of(vds.getType()), null, true));
        } else if (statement instanceof FunctionDefinition fd) {
            // Create a new frame
            stack.newFrame();

            // Set arguments in the new frame
            Frame frame = stack.peek();
            var arguments = fd.getArguments();
            for (var a : arguments) {
                frame.put(a.getName(), new Variable(a.getName(), a.getType(), null, true));
            }

            // Add function definition to global list
            String signature = fd.getSignature();
            if (functions.containsKey(signature)) {
                throw new ValidationError(
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

            // Set current definition as being evaluated
            functionUnderEvaluation = fd;

            // Validate body (including return statements)
            validate(fd.getBody());

            functionUnderEvaluation = null;

            // Pop frame
            stack.pop();
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
            Frame frame = stack.peek();
            if (!frame.containsKey(identifier.getName())) {
                throw new ValidationError(String.format("Undeclared variable '%s'", identifier.getName()),
                        identifier.getPosition(), identifier.getLength());
            }
            return frame.get(identifier.getName()).getType();
        }
        if (expression instanceof FunctionCallExpression call) {
            String name = call.getName();
            String signature = name;
            List<String> argTypes = new ArrayList<>();
            for (Expression e : call.getArguments()) {
                argTypes.add(getType(e).getName().toLowerCase());
            }
            if (!argTypes.isEmpty()) {
                signature += "_" + String.join("_", argTypes);
            }
            if (!functions.containsKey(signature)) {
                throw new ValidationError(
                        String.format("Undeclared function %s(%s)", name, String.join(",", argTypes)),
                        call.getPosition(), call.getLength());
            }
            return functions.get(signature).getReturnType();
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
            if ("!".equals(operator)) {
                if (operandType != Type.BOOLEAN) {
                    throw new ValidationError(
                            String.format("Operation '%s' cannot be applied to '%s'", operator, operandType.getName()),
                            unaryExpression.getOperatorPosition(),
                            unaryExpression.getOperatorLength()
                    );
                }
                return Type.BOOLEAN;
            }
            throw new ValidationError(
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
                if ("&&".equals(operator)) {
                    return logicalAnd(left, right);
                }
                if ("||".equals(operator)) {
                    return logicalOr(left, right);
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
            return Type.BOOLEAN;
        }
        throw new RuntimeException();
    }

    Type greaterThanOrEqual(Type left, Type right) {
        if (left == Type.NUMBER && right == Type.NUMBER) {
            return Type.BOOLEAN;
        }
        throw new RuntimeException();
    }

    Type lessThan(Type left, Type right) {
        if (left == Type.NUMBER && right == Type.NUMBER) {
            return Type.BOOLEAN;
        }
        throw new RuntimeException();
    }

    Type lessThanOrEqual(Type left, Type right) {
        if (left == Type.NUMBER && right == Type.NUMBER) {
            return Type.BOOLEAN;
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

    Type logicalAnd(Type left, Type right) {
        if (left == Type.BOOLEAN && right == Type.BOOLEAN) {
            return Type.BOOLEAN;
        }
        throw new RuntimeException();
    }

    Type logicalOr(Type left, Type right) {
        if (left == Type.BOOLEAN && right == Type.BOOLEAN) {
            return Type.BOOLEAN;
        }
        throw new RuntimeException();
    }

    Type assign(BinaryExpression binaryExpression) throws ValidationError {
        if (!(binaryExpression.getLeft() instanceof Identifier identifier)) {
            throw new ValidationError("Left side of assignment must be a variable",
                    binaryExpression.getLeft().getPosition(), binaryExpression.getLeft().getLength());
        }
        Frame frame = stack.peek();
        if (!frame.containsKey(identifier.getName())) {
            throw new ValidationError(String.format("Undeclared variable '%s'", identifier.getName()),
                    identifier.getPosition(), identifier.getLength());
        }
        Type variableType = frame.get(identifier.getName()).getType();
        Type right = getType(binaryExpression.getRight());
        if (variableType == right) {
            return right;
        }
        throw new ValidationError(String.format("Cannot assign value of type '%s' to variable of type '%s'",
                right.getName(), variableType.getName()),
                binaryExpression.getOperatorPosition(), binaryExpression.getOperatorLength());
    }
}
