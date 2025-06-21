package org.senthilvsh.saffron.validate;

import org.senthilvsh.saffron.ast.*;
import org.senthilvsh.saffron.common.Scope;
import org.senthilvsh.saffron.common.Scopes;
import org.senthilvsh.saffron.common.Type;
import org.senthilvsh.saffron.common.Variable;
import org.senthilvsh.saffron.stdlib.NativeFunctionsRegistry;

import java.util.*;
import java.util.stream.Collectors;

import static org.senthilvsh.saffron.common.Type.STRING;
import static org.senthilvsh.saffron.common.Type.VOID;

public class Validator {
    private final Scopes scopes = new Scopes();
    private final Map<String, FunctionDefinition> functions = new HashMap<>();

    private final Stack<Statement> validationStack = new Stack<>();

    public Validator() {
        functions.putAll(NativeFunctionsRegistry.getAll());
        scopes.addFunctionScope();
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
            validateBlockStatement(bs);
        } else if (statement instanceof ReturnStatement rs) {
            validateReturnStatement(rs);
        } else if (statement instanceof ConditionalStatement cs) {
            validateConditionalStatement(cs);
        } else if (statement instanceof WhileLoop wl) {
            validateWhileLoop(wl);
        } else if (statement instanceof ContinueStatement cs) {
            validateContinueStatement(cs);
        } else if (statement instanceof BreakStatement bs) {
            validateBreakStatement(bs);
        } else if (statement instanceof TryCatchStatement tcs) {
            validateTryCatchStatement(tcs);
        } else if (statement instanceof VariableDeclaration vds) {
            validateVariableDeclaration(vds);
        } else if (statement instanceof FunctionDefinition fd) {
            validateFunctionDefinition(fd);
        }
    }

    private void validateBlockStatement(BlockStatement bs) throws ValidationError {
        List<Statement> statements = bs.getStatements();
        for (Statement s : statements) {
            validate(s);
        }
    }

    private void validateReturnStatement(ReturnStatement rs) throws ValidationError {
        if (validationStack.isEmpty() || !(validationStack.peek() instanceof FunctionDefinition containingFunction)) {
            throw new ValidationError(
                    "A 'return' statement can only be present inside a function",
                    rs.getPosition(),
                    rs.getLength()
            );
        }
        Expression expression = rs.getExpression();
        Type type = VOID;
        if (expression != null) {
            type = getType(expression);
        }

        Type returnType = containingFunction.getReturnType();
        if (type != returnType) {
            throw new ValidationError(
                    String.format("Must return a value of type '%s'", returnType.getName()),
                    rs.getPosition(),
                    rs.getLength());
        }
    }

    private void validateConditionalStatement(ConditionalStatement cs) throws ValidationError {
        Expression condition = cs.getCondition();
        Type conditionType = getType(condition);
        if (conditionType != Type.BOOLEAN) {
            throw new ValidationError("The condition of an 'if' statement must be a boolean expression",
                    condition.getPosition(), condition.getLength());
        }
        scopes.addBlockScope();
        validate(cs.getTrueClause());
        scopes.remove();
        if (cs.getFalseClause() != null) {
            scopes.addBlockScope();
            validate(cs.getFalseClause());
            scopes.remove();
        }
    }

    private void validateWhileLoop(WhileLoop wl) throws ValidationError {
        Expression condition = wl.getCondition();
        Type conditionType = getType(condition);
        if (conditionType != Type.BOOLEAN) {
            throw new ValidationError("The condition of a 'while' loop must be a boolean expression",
                    condition.getPosition(), condition.getLength());
        }
        scopes.addBlockScope();
        validationStack.push(wl);
        validate(wl.getBody());
        validationStack.pop();
        scopes.remove();
    }

    private void validateContinueStatement(ContinueStatement cs) throws ValidationError {
        if (validationStack.isEmpty() || !(validationStack.peek() instanceof WhileLoop)) {
            throw new ValidationError(
                    "A 'continue' statement can be present only inside a loop",
                    cs.getPosition(),
                    cs.getLength()
            );
        }
    }

    private void validateBreakStatement(BreakStatement bs) throws ValidationError {
        if (validationStack.isEmpty() || !(validationStack.peek() instanceof WhileLoop)) {
            throw new ValidationError(
                    "A 'break' statement can be present only inside a loop",
                    bs.getPosition(),
                    bs.getLength()
            );
        }
    }

    private void validateTryCatchStatement(TryCatchStatement tcs) throws ValidationError {
        scopes.addBlockScope();
        validate(tcs.getTryBlock());
        scopes.remove();
        CatchBlockArgument exType = tcs.getExceptionType();
        if (exType.getType() != STRING) {
            throw new ValidationError("The type of the first argument must be string", exType.getPosition(), exType.getLength());
        }
        CatchBlockArgument exMsg = tcs.getExceptionMessage();
        if (exMsg.getType() != STRING) {
            throw new ValidationError("The type of the second argument must be string", exMsg.getPosition(), exMsg.getLength());
        }
        scopes.addBlockScope();
        String typeArgName = tcs.getExceptionType().getName();
        Variable typeArgVariable = new Variable(
                typeArgName,
                Type.STRING,
                null,
                scopes.getDepth()
        );

        String msgArgName = tcs.getExceptionMessage().getName();
        Variable msgArgVariable = new Variable(
                msgArgName,
                Type.STRING,
                null,
                scopes.getDepth()
        );

        scopes.current().put(typeArgName, typeArgVariable);
        scopes.current().put(msgArgName, msgArgVariable);
        validate(tcs.getCatchBlock());
        scopes.remove();
    }

    private void validateVariableDeclaration(VariableDeclaration vds) throws ValidationError {
        String name = vds.getName();
        Type variableType = Type.of(vds.getType());
        if (variableType == VOID) {
            throw new ValidationError(String.format("Variables cannot have '%s' type", VOID.getName()),
                    vds.getPosition(), vds.getLength());
        }
        Scope scope = scopes.current();
        if (scope.containsKey(name) && scope.get(name).getScopeDepth() == scopes.getDepth()) {
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

        scope.put(name, new Variable(name, Type.of(vds.getType()), null, scopes.getDepth()));
    }

    private void validateFunctionDefinition(FunctionDefinition fd) throws ValidationError {
        // Create a new frame
        scopes.addFunctionScope();

        // Set arguments in the new frame
        Scope scope = scopes.current();
        var arguments = fd.getArguments();
        for (var a : arguments) {
            scope.put(a.getName(), new Variable(a.getName(), a.getType(), null, scopes.getDepth()));
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

        validationStack.push(fd);

        // Validate body (including return statements)
        validate(fd.getBody());

        validationStack.pop();

        // Pop frame
        scopes.remove();
    }

    private Type getType(Expression expression) throws ValidationError {
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
            Scope scope = scopes.current();
            if (!scope.containsKey(identifier.getName())) {
                throw new ValidationError(String.format("Undeclared variable '%s'", identifier.getName()),
                        identifier.getPosition(), identifier.getLength());
            }
            return scope.get(identifier.getName()).getType();
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
        Scope scope = scopes.current();
        if (!scope.containsKey(identifier.getName())) {
            throw new ValidationError(String.format("Undeclared variable '%s'", identifier.getName()),
                    identifier.getPosition(), identifier.getLength());
        }
        Type variableType = scope.get(identifier.getName()).getType();
        Type right = getType(binaryExpression.getRight());
        if (variableType == right) {
            return right;
        }
        throw new ValidationError(String.format("Cannot assign value of type '%s' to variable of type '%s'",
                right.getName(), variableType.getName()),
                binaryExpression.getOperatorPosition(), binaryExpression.getOperatorLength());
    }
}
