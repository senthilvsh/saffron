package org.senthilvsh.saffron.runtime;

import org.senthilvsh.saffron.ast.*;
import org.senthilvsh.saffron.stdlib.NativeFunctionsRegistry;

import java.util.*;

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
            return executeExpressionStatement(es);
        } else if (statement instanceof BlockStatement bs) {
            return executeBlockStatement(bs);
        } else if (statement instanceof ReturnStatement rs) {
            return executeReturnStatement(rs);
        } else if (statement instanceof ConditionalStatement cs) {
            return executeConditionalStatement(cs);
        } else if (statement instanceof TryCatchStatement tcs) {
            return executeTryCatchStatement(tcs);
        } else if (statement instanceof WhileLoop wl) {
            return executeWhileLoop(wl);
        } else if (statement instanceof BreakStatement bs) {
            return executeBreakStatement(bs);
        } else if (statement instanceof ContinueStatement cs) {
            return executeContinueStatement(cs);
        } else if (statement instanceof VariableDeclaration vd) {
            return executeVariableDeclaration(vd);
        } else if (statement instanceof FunctionDefinition fd) {
            return executeFunctionDefinition(fd);
        }

        return new StatementResult(StatementResultType.NORMAL);
    }

    private StatementResult executeExpressionStatement(ExpressionStatement es) throws RuntimeError {
        evaluate(es.getExpression());
        return new StatementResult(StatementResultType.NORMAL);
    }

    private StatementResult executeBlockStatement(BlockStatement bs) throws RuntimeError {
        List<Statement> statements = bs.getStatements();
        StatementResult result = new StatementResult(StatementResultType.NORMAL);
        for (Statement s : statements) {
            result = execute(s);
            if (result.getType() != StatementResultType.NORMAL) {
                break;
            }
        }
        return result;
    }

    private StatementResult executeReturnStatement(ReturnStatement rs) throws RuntimeError {
        if (validationStack.isEmpty() || !(validationStack.peek() instanceof FunctionDefinition containingFunction)) {
            throw new RuntimeError(
                    "A 'return' statement can only be present inside a function",
                    rs.getPosition(),
                    rs.getLength()
            );
        }
        Expression expression = rs.getExpression();
        Object returnValue = null;
        if (expression != null) {
            returnValue = evaluate(expression);
        }

        return new ReturnStatementResult(returnValue);
    }

    private StatementResult executeConditionalStatement(ConditionalStatement cs) throws RuntimeError {
        Expression condition = cs.getCondition();
        Object baseObj = evaluate(condition);
        if (!(baseObj instanceof Boolean conditionResult)) {
            throw new RuntimeError("The condition of an 'if' statement must be a boolean expression",
                    condition.getPosition(), condition.getLength());
        }
        if (conditionResult) {
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
        return new StatementResult(StatementResultType.NORMAL);
    }

    private StatementResult executeTryCatchStatement(TryCatchStatement tcs) throws RuntimeError {
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

            String typeArgName = tcs.getExceptionType();
            String typeArgValue = ex.getType();
            Variable typeArgVariable = new Variable(
                    typeArgName,
                    typeArgValue,
                    scopes.getDepth()
            );

            String msgArgName = tcs.getExceptionMessage();
            String msgArgValue = ex.getMessage();
            Variable msgArgVariable = new Variable(
                    msgArgName,
                    msgArgValue,
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
    }

    private StatementResult executeWhileLoop(WhileLoop wl) throws RuntimeError {
        Expression condition = wl.getCondition();
        Object baseObj = evaluate(condition);
        if (!(baseObj instanceof Boolean conditionResult)) {
            throw new RuntimeError("The condition of a 'while' loop must be a boolean expression",
                    condition.getPosition(), condition.getLength());
        }

        validationStack.push(wl);
        while (conditionResult != null && conditionResult) {
            scopes.addBlockScope();
            StatementResult result = execute(wl.getBody());
            scopes.remove();
            if (result.getType() == StatementResultType.BREAK) {
                break;
            }
            conditionResult = (Boolean) evaluate(condition);
        }
        validationStack.pop();
        return new StatementResult(StatementResultType.NORMAL);
    }

    private StatementResult executeBreakStatement(BreakStatement bs) throws RuntimeError {
        if (validationStack.isEmpty() || !(validationStack.peek() instanceof WhileLoop)) {
            throw new RuntimeError(
                    "A 'break' statement can be present only inside a loop",
                    bs.getPosition(),
                    bs.getLength()
            );
        }
        return new StatementResult(StatementResultType.BREAK);
    }

    private StatementResult executeContinueStatement(ContinueStatement cs) throws RuntimeError {
        if (validationStack.isEmpty() || !(validationStack.peek() instanceof WhileLoop)) {
            throw new RuntimeError(
                    "A 'continue' statement can be present only inside a loop",
                    cs.getPosition(),
                    cs.getLength()
            );
        }
        return new StatementResult(StatementResultType.CONTINUE);
    }

    private StatementResult executeVariableDeclaration(VariableDeclaration vd) throws RuntimeError {
        String name = vd.getName();
        Scope scope = scopes.current();
        if (scope.containsKey(name) && scope.get(name).getScopeDepth() == scopes.getDepth()) {
            throw new RuntimeError(String.format("Re-declaration of variable '%s'", name),
                    vd.getPosition(), vd.getLength());
        }
        Object initValue = null;
        if (vd.getExpression() != null) {
            initValue = evaluate(vd.getExpression());
        }
        Variable v = new Variable(name, initValue, scopes.getDepth());
        scope.put(name, v);
        return new StatementResult(StatementResultType.NORMAL);
    }

    private StatementResult executeFunctionDefinition(FunctionDefinition fd) throws RuntimeError {
        String name = fd.getName();
        if (functions.containsKey(name)) {
            throw new RuntimeError(
                    String.format("Function re-declaration: %s", fd.getName()),
                    fd.getNamePosition(),
                    fd.getNameLength());
        }
        functions.put(name, fd);
        return new StatementResult(StatementResultType.NORMAL);
    }

    private Object evaluate(Expression expression) throws RuntimeError {
        if (expression instanceof NumberLiteral n) {
            return n.getValue();
        }
        if (expression instanceof StringLiteral s) {
            return s.getValue();
        }
        if (expression instanceof BooleanLiteral b) {
            return b.getValue();
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
            List<Object> args = new ArrayList<>();
            for (Expression e : call.getArguments()) {
                args.add(evaluate(e));
            }
            if (!functions.containsKey(name)) {
                throw new RuntimeError(
                        String.format("Undeclared function %s", name),
                        call.getPosition(), call.getLength());
            }
            FunctionDefinition fd = functions.get(name);
            scopes.addFunctionScope();
            Scope scope = scopes.current();
            var arguments = fd.getArguments();
            for (int i = 0; i < arguments.size(); i++) {
                scope.put(arguments.get(i), new Variable(arguments.get(i), args.get(i), scopes.getDepth()));
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
                result = execute(functions.get(name).getBody());
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
            Object baseObj = evaluate(operand);
            if ("+-".contains(operator)) {
                if (!(baseObj instanceof Double numberObj)) {
                    throw new RuntimeError(
                            String.format("Operation '%s' cannot be applied to '%s'", operator, baseObj.getClass()),
                            unaryExpression.getOperatorPosition(),
                            unaryExpression.getOperatorLength()
                    );
                }
                if ("+".equals(operator)) {
                    return numberObj;
                }
                if ("-".equals(operator)) {
                    return -1 * numberObj;
                }
            }
            if ("!".contains(operator)) {
                if (!(baseObj instanceof Boolean booleanObj)) {
                    throw new RuntimeError(
                            String.format("Operation '%s' cannot be applied to '%s'", operator, baseObj.getClass()),
                            unaryExpression.getOperatorPosition(),
                            unaryExpression.getOperatorLength()
                    );
                }
                return !booleanObj;
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

            Object left = evaluate(binaryExpression.getLeft());
            Object right = evaluate(binaryExpression.getRight());

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
                        left.getClass(), right.getClass()), binaryExpression.getOperatorPosition(), binaryExpression.getOperatorLength());
            }
        }
        throw new RuntimeError("Unknown expression type", expression.getPosition(), expression.getLength());
    }

    private Object add(Object left, Object right) {
        if (left instanceof Double l && right instanceof Double r) {
            return l + r;
        }
        if (left instanceof String l || right instanceof String r) {
            String leftStr = left.toString();
            String rightStr = right.toString();
            return leftStr + rightStr;
        }
        throw new RuntimeException();
    }

    private Object subtract(Object left, Object right) {
        if (left instanceof Double l && right instanceof Double r) {
            return l - r;
        }
        throw new RuntimeException();
    }

    private Object multiply(Object left, Object right) {
        if (left instanceof Double l && right instanceof Double r) {
            return l * r;
        }
        throw new RuntimeException();
    }

    private Object divide(Object left, Object right) {
        if (left instanceof Double l && right instanceof Double r) {
            return l / r;
        }
        throw new RuntimeException();
    }

    private Object modulo(Object left, Object right) {
        if (left instanceof Double l && right instanceof Double r) {
            return l % r;
        }
        throw new RuntimeException();
    }

    private Object greaterThan(Object left, Object right) {
        if (left instanceof Double l && right instanceof Double r) {
            return l > r;
        }
        throw new RuntimeException();
    }

    private Object greaterThanOrEqual(Object left, Object right) {
        if (left instanceof Double l && right instanceof Double r) {
            return l >= r;
        }
        throw new RuntimeException();
    }

    private Object lessThan(Object left, Object right) {
        if (left instanceof Double l && right instanceof Double r) {
            return l < r;
        }
        throw new RuntimeException();
    }

    private Object lessThanOrEqual(Object left, Object right) {
        if (left instanceof Double l && right instanceof Double r) {
            return l <= r;
        }
        throw new RuntimeException();
    }

    private Object equal(Object left, Object right) {
        if (left.getClass().equals(right.getClass())) {
            return left.equals(right);
        }
        // Types of left and right are different
        throw new RuntimeException();
    }

    private Object notEqual(Object left, Object right) {
        if (left.getClass().equals(right.getClass())) {
            return !left.equals(right);
        }
        // Types of left and right are different
        throw new RuntimeException();
    }

    private Object logicalAnd(Object left, Object right) {
        if (left instanceof Boolean l && right instanceof Boolean r) {
            return l && r;
        }
        throw new RuntimeException();
    }

    private Object logicalOr(Object left, Object right) {
        if (left instanceof Boolean l && right instanceof Boolean r) {
            return l || r;
        }
        throw new RuntimeException();
    }

    private Object assign(BinaryExpression binaryExpression) throws RuntimeError {
        String variableName = getVariableName(binaryExpression);

        Object right = evaluate(binaryExpression.getRight());

        Variable variable = scopes.current().get(variableName);
        variable.setValue(right);

        return right;
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
}
