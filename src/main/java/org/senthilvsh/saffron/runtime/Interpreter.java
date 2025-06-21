package org.senthilvsh.saffron.runtime;

import org.senthilvsh.saffron.ast.*;
import org.senthilvsh.saffron.common.Scope;
import org.senthilvsh.saffron.common.Scopes;
import org.senthilvsh.saffron.common.Type;
import org.senthilvsh.saffron.common.Variable;
import org.senthilvsh.saffron.stdlib.NativeFunctionsRegistry;

import java.util.*;
import java.util.stream.Collectors;

public class Interpreter {
    private final Stack<Statement> validationStack = new Stack<>();
    private final Scopes scopes = new Scopes();
    private final Map<String, FunctionDefinition> functions = new HashMap<>();

    public Interpreter() {
        functions.putAll(NativeFunctionsRegistry.getAll());
        scopes.addFunctionScope();
    }

    public void execute(Program program) throws RuntimeError {
        for (Statement s : program.getStatements()) {
            execute(s);
        }
    }

    private StatementResult execute(Statement statement) throws RuntimeError {
        if (statement instanceof ExpressionStatement es) {
            evaluate(es.getExpression());
            return new StatementResult(StatementResultType.NORMAL);
        } else if (statement instanceof BlockStatement bs) {
            List<Statement> statements = bs.getStatements();
            StatementResult result = new StatementResult(StatementResultType.NORMAL);
            for (Statement s : statements) {
                result = execute(s);
                if (result.getType() != StatementResultType.NORMAL) {
                    break;
                }
            }
            return result;
        } else if (statement instanceof ReturnStatement rs) {
            if (validationStack.isEmpty() || !(validationStack.peek() instanceof FunctionDefinition containingFunction)) {
                throw new RuntimeError(
                        "A 'return' statement can only be present inside a function",
                        rs.getPosition(),
                        rs.getLength()
                );
            }
            Expression expression = rs.getExpression();
            BaseObj returnValue = null;
            if (expression != null) {
                returnValue = evaluate(expression);
            }

            if (returnValue != null && returnValue.getType() != containingFunction.getReturnType()) {
                throw new RuntimeError(
                        "The type of the return value does not match the function's return type",
                        rs.getPosition(),
                        rs.getLength()
                );
            }

            return new ReturnStatementResult(returnValue);
        } else if (statement instanceof ConditionalStatement cs) {
            Expression condition = cs.getCondition();
            BaseObj baseObj = evaluate(condition);
            if (baseObj.getType() != Type.BOOLEAN) {
                throw new RuntimeError("The condition of an 'if' statement must be a boolean expression",
                        condition.getPosition(), condition.getLength());
            }
            BooleanObj conditionResult = (BooleanObj) baseObj;
            if (conditionResult.getValue()) {
                StatementResult result;
                scopes.addBlockScope();
                result = execute(cs.getTrueClause());
                scopes.remove();
                return result;
            } else {
                if (cs.getFalseClause() != null) {
                    StatementResult result;
                    scopes.addBlockScope();
                    result = execute(cs.getFalseClause());
                    scopes.remove();
                    return result;
                }
            }
        } else if (statement instanceof TryCatchStatement tcs) {
            StatementResult result;
            Statement tryBlock = tcs.getTryBlock();

            try {
                scopes.addBlockScope();
                result = execute(tryBlock);
                scopes.remove();
                return result;
            } catch (NativeFunctionException ex) {
                scopes.remove();

                Statement catchBlock = tcs.getCatchBlock();

                scopes.addBlockScope();

                String typeArgName = tcs.getExceptionType().getName();
                String typeArgValue = ex.getType();
                Variable typeArgVariable = new Variable(
                        typeArgName,
                        Type.STRING,
                        new StringObj(typeArgValue),
                        scopes.getDepth()
                );

                String msgArgName = tcs.getExceptionMessage().getName();
                String msgArgValue = ex.getMessage();
                Variable msgArgVariable = new Variable(
                        msgArgName,
                        Type.STRING,
                        new StringObj(msgArgValue),
                        scopes.getDepth()
                );

                scopes.current().put(typeArgName, typeArgVariable);
                scopes.current().put(msgArgName, msgArgVariable);

                try {
                    execute(catchBlock);
                    return new StatementResult(StatementResultType.NORMAL);
                } catch (NativeFunctionException catchBlockException) {
                    scopes.remove();
                    // This is the case where code inside the catch block throws an exception,
                    // and it is not handled with a nested try-catch. All we can do it throw it upstream.
                    throw catchBlockException;
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
            validationStack.push(wl);
            while (conditionResult.getValue()) {
                scopes.addBlockScope();
                StatementResult result = execute(wl.getBody());
                scopes.remove();
                if (result.getType() == StatementResultType.BREAK) {
                    break;
                }
                conditionResult = (BooleanObj) evaluate(condition);
            }
            validationStack.pop();
            return new StatementResult(StatementResultType.NORMAL);
        } else if (statement instanceof BreakStatement bs) {
            if (validationStack.isEmpty() || !(validationStack.peek() instanceof WhileLoop)) {
                throw new RuntimeError(
                        "A 'break' statement can be present only inside a loop",
                        bs.getPosition(),
                        bs.getLength()
                );
            }
            return new StatementResult(StatementResultType.BREAK);
        } else if (statement instanceof ContinueStatement cs) {
            if (validationStack.isEmpty() || !(validationStack.peek() instanceof WhileLoop)) {
                throw new RuntimeError(
                        "A 'continue' statement can be present only inside a loop",
                        cs.getPosition(),
                        cs.getLength()
                );
            }
            return new StatementResult(StatementResultType.CONTINUE);
        } else if (statement instanceof VariableDeclaration vds) {
            String name = vds.getName();
            Type variableType = Type.of(vds.getType());
            Scope scope = scopes.current();
            if (scope.containsKey(name) && scope.get(name).getScopeDepth() == scopes.getDepth()) {
                throw new RuntimeError(String.format("Re-declaration of variable '%s'", name),
                        vds.getPosition(), vds.getLength());
            }
            if (variableType == Type.VOID) {
                throw new RuntimeError(String.format("Variables cannot have '%s' type", Type.VOID.getName()),
                        vds.getPosition(), vds.getLength());
            }
            BaseObj initValue = null;
            if (vds.getExpression() != null) {
                initValue = evaluate(vds.getExpression());
                if (initValue.getType() != variableType) {
                    throw new RuntimeError(
                            String.format("Value of type '%s' cannot be assigned to variable of type '%s'",
                                    initValue.getType().getName(), variableType.getName()),
                            vds.getExpression().getPosition(),
                            vds.getExpression().getLength()
                    );
                }
            }
            Variable v = new Variable(name, Type.of(vds.getType()), initValue, scopes.getDepth());
            scope.put(name, v);
            return new StatementResult(StatementResultType.NORMAL);
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
            return new StatementResult(StatementResultType.NORMAL);
        }

        return new StatementResult(StatementResultType.NORMAL);
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
            Scope scope = scopes.current();
            if (!scope.containsKey(i.getName())) {
                throw new RuntimeError(String.format("Undefined variable '%s'", i.getName()),
                        i.getPosition(), i.getLength());
            }
            Variable variable = scope.get(i.getName());
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
            List<String> argTypes = args.stream().map(a -> a == null ? "" : a.getType().getName().toLowerCase()).toList();
            if (!argTypes.isEmpty()) {
                signature += "_" + String.join("_", argTypes);
            }
            if (!functions.containsKey(signature)) {
                throw new RuntimeError(
                        String.format("Undeclared function %s(%s)", name, String.join(",", argTypes)),
                        call.getPosition(), call.getLength());
            }
            FunctionDefinition fd = functions.get(signature);
            scopes.addFunctionScope();
            Scope scope = scopes.current();
            var arguments = fd.getArguments();
            for (int i = 0; i < arguments.size(); i++) {
                scope.put(arguments.get(i).getName(), new Variable(arguments.get(i).getName(), arguments.get(i).getType(), args.get(i), scopes.getDepth()));
            }

            validationStack.push(fd);

            StatementResult result;
            if (fd instanceof NativeFunctionDefinition nfd) {
                try {
                    result = nfd.getFunction().run(scope);
                } catch (NativeFunctionException ex) {
                    validationStack.pop();
                    scopes.remove();
                    throw ex;
                }
            } else {
                result = execute(functions.get(signature).getBody());
            }

            validationStack.pop();
            scopes.remove();

            if (result instanceof ReturnStatementResult rsr) {
                return rsr.getReturnValue();
            }

            return null;
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

        Scope scope = scopes.current();
        Variable variable = scope.get(variableName);
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

        Scope scope = scopes.current();
        String variableName = identifier.getName();
        if (!scope.containsKey(variableName)) {
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
