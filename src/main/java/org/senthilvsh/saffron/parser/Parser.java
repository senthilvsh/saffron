package org.senthilvsh.saffron.parser;

import org.senthilvsh.saffron.ast.*;
import org.senthilvsh.saffron.common.Type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.senthilvsh.saffron.parser.TokenType.*;

public class Parser {
    private final List<Token> tokens = new ArrayList<>();
    private int tokenIdx = 0;
    private Token lookahead;

    public Parser(String source) {
        Lexer lexer = new Lexer(source);

        Token token = lexer.next();
        while (token != null) {
            if (token.getType() != COMMENT) {
                tokens.add(token);
            }
            token = lexer.next();
        }

        if (!tokens.isEmpty()) {
            lookahead = tokens.get(0);
        }
    }

    public Program parse() throws ParseError {
        List<Statement> statements = new ArrayList<>();
        while (lookahead != null) {
            statements.add(statement());
        }
        return new Program(statements);
    }

    Statement statement() throws ParseError {
        assertLookAheadNotNull();

        if (lookahead.getType() == TokenType.KEYWORD && lookahead.getValue().equals("fun")) {
            return functionDefinition();
        }

        if (lookahead.getType() == TokenType.KEYWORD && lookahead.getValue().equals("var")) {
            return variableDeclarationStatement();
        }

        if (lookahead.getType() == TokenType.KEYWORD && lookahead.getValue().equals("return")) {
            return returnStatement();
        }

        if (lookahead.getType() == TokenType.KEYWORD && lookahead.getValue().equals("if")) {
            return conditionalStatement();
        }

        if (lookahead.getType() == TokenType.KEYWORD && lookahead.getValue().equals("while")) {
            return whileStatement();
        }

        if (lookahead.getType() == TokenType.KEYWORD && lookahead.getValue().equals("continue")) {
            return continueStatement();
        }

        if (lookahead.getType() == TokenType.KEYWORD && lookahead.getValue().equals("break")) {
            return breakStatement();
        }

        if (lookahead.getType() == TokenType.KEYWORD && lookahead.getValue().equals("try")) {
            return tryCatchStatement();
        }

        if (lookahead.getType() == TokenType.SYMBOL && lookahead.getValue().equals("{")) {
            return blockStatement();
        }

        return expressionStatement();
    }

    Statement functionDefinition() throws ParseError {
        Token funKeyword = consume(TokenType.KEYWORD, new String[]{"fun"});
        Token functionName = consume(TokenType.IDENTIFIER);

        consume(TokenType.SYMBOL, new String[]{"("});

        List<FunctionArgument> arguments = new ArrayList<>();

        assertLookAheadNotNull();
        while (!lookahead.getValue().equals(")")) {
            Token argName = consume(IDENTIFIER);
            consume(SYMBOL, new String[]{":"});
            Token type = consume(KEYWORD, new String[]{"num", "str", "bool"});
            arguments.add(new FunctionArgument(argName.getValue(), Type.of(type.getValue())));
            if (!lookahead.getValue().equals(",")) {
                break;
            }
            consume(SYMBOL, new String[]{","});
        }

        consume(TokenType.SYMBOL, new String[]{")"});
        consume(TokenType.SYMBOL, new String[]{":"});
        Token returnType = consume(KEYWORD, new String[]{"void", "num", "str", "bool"});

        Statement body = blockStatement();

        return new FunctionDefinition(
                functionName.getValue(),
                arguments,
                (BlockStatement) body,
                Type.of(returnType.getValue()),
                funKeyword.getPosition(), body.getPosition() + body.getLength() - funKeyword.getPosition(),
                functionName.getPosition(),
                functionName.getLength()
        );
    }

    Statement variableDeclarationStatement() throws ParseError {
        Token varKeyword = consume(TokenType.KEYWORD, new String[]{"var"});
        Token variableName = consume(TokenType.IDENTIFIER);
        consume(TokenType.SYMBOL, new String[]{":"});
        Token typeSpecifier = consume(TokenType.KEYWORD, new String[]{"num", "str", "bool"});

        Expression expression = null;

        // Variable Initialization
        assertLookAheadNotNull();
        if (lookahead.getValue().equals("=")) {
            consume(OPERATOR, new String[]{"="});
            expression = expression();
        }

        Token semicolon = consume(TokenType.SYMBOL, new String[]{";"});

        return new VariableDeclaration(
                variableName.getValue(),
                typeSpecifier.getValue(),
                expression,
                varKeyword.getPosition(),
                semicolon.getPosition() + semicolon.getLength() - varKeyword.getPosition()
        );
    }

    Statement returnStatement() throws ParseError {
        Token returnKeyword = consume(TokenType.KEYWORD, new String[]{"return"});
        Expression expression = null;
        assertLookAheadNotNull();
        if (!lookahead.getValue().equals(";")) {
            expression = expression();
        }
        Token semicolon = consume(TokenType.SYMBOL, new String[]{";"});
        return new ReturnStatement(
                expression,
                returnKeyword.getPosition(),
                semicolon.getPosition() + semicolon.getLength() - returnKeyword.getPosition()
        );
    }

    Statement conditionalStatement() throws ParseError {
        Token ifKeyword = consume(TokenType.KEYWORD, new String[]{"if"});
        consume(TokenType.SYMBOL, new String[]{"("});
        Expression condition = equalityExpression();
        consume(TokenType.SYMBOL, new String[]{")"});
        Statement trueClause = statement();
        if (lookahead == null || !(lookahead.getType() == TokenType.KEYWORD && lookahead.getValue().equals("else"))) {
            return new ConditionalStatement(
                    condition,
                    trueClause,
                    null,
                    ifKeyword.getPosition(),
                    trueClause.getPosition() + trueClause.getLength() - ifKeyword.getPosition()
            );
        }
        consume(TokenType.KEYWORD, new String[]{"else"});
        Statement falseClause = statement();
        return new ConditionalStatement(
                condition,
                trueClause,
                falseClause,
                ifKeyword.getPosition(),
                falseClause.getPosition() + falseClause.getLength() - ifKeyword.getPosition()
        );
    }

    Statement whileStatement() throws ParseError {
        Token whileKeyword = consume(TokenType.KEYWORD, new String[]{"while"});
        consume(TokenType.SYMBOL, new String[]{"("});
        Expression condition = equalityExpression();
        consume(TokenType.SYMBOL, new String[]{")"});
        Statement loopBody = statement();
        return new WhileLoop(
                condition,
                loopBody,
                whileKeyword.getPosition(),
                loopBody.getPosition() + loopBody.getLength() - whileKeyword.getPosition()
        );
    }

    Statement continueStatement() throws ParseError {
        Token continueKeyword = consume(TokenType.KEYWORD, new String[]{"continue"});
        Token semicolon = consume(TokenType.SYMBOL, new String[]{";"});
        return new ContinueStatement(
                continueKeyword.getPosition(),
                semicolon.getPosition() + semicolon.getLength() - continueKeyword.getPosition()
        );
    }

    Statement breakStatement() throws ParseError {
        Token breakKeyword = consume(TokenType.KEYWORD, new String[]{"break"});
        Token semicolon = consume(TokenType.SYMBOL, new String[]{";"});
        return new BreakStatement(
                breakKeyword.getPosition(),
                semicolon.getPosition() + semicolon.getLength() - breakKeyword.getPosition()
        );
    }

    Statement tryCatchStatement() throws ParseError {
        assertLookAheadNotNull();
        Token tryKeyword = consume(KEYWORD, new String[]{"try"});
        Statement tryBlock = blockStatement();
        consume(KEYWORD, new String[]{"catch"});
        consume(SYMBOL, new String[]{"("});
        Token typeArgName = consume(IDENTIFIER);
        consume(SYMBOL, new String[]{":"});
        Token typeArgType = consume(KEYWORD);
        consume(SYMBOL, new String[]{","});
        Token msgArgName = consume(IDENTIFIER);
        consume(SYMBOL, new String[]{":"});
        Token msgArgType = consume(KEYWORD);
        consume(SYMBOL, new String[]{")"});
        Statement catchBlock = blockStatement();
        return new TryCatchStatement(
                tryBlock,
                new CatchBlockArgument(
                        typeArgName.getValue(),
                        Type.of(typeArgType.getValue()),
                        typeArgName.getPosition(),
                        typeArgType.getPosition() + typeArgType.getLength() - typeArgName.getPosition()),
                new CatchBlockArgument(
                        msgArgName.getValue(),
                        Type.of(msgArgType.getValue()),
                        msgArgName.getPosition(),
                        msgArgType.getPosition() + msgArgType.getLength() - msgArgName.getPosition()),
                catchBlock,
                tryKeyword.getPosition(),
                catchBlock.getPosition() + catchBlock.getLength() - tryKeyword.getPosition()
        );
    }

    Statement blockStatement() throws ParseError {
        Token open = consume(TokenType.SYMBOL, new String[]{"{"});
        List<Statement> statements = new ArrayList<>();
        while (!lookahead.getValue().equals("}")) {
            statements.add(statement());
        }
        Token close = consume(TokenType.SYMBOL, new String[]{"}"});
        return new BlockStatement(
                statements,
                open.getPosition(),
                close.getPosition() + close.getLength() - open.getPosition()
        );
    }

    Statement expressionStatement() throws ParseError {
        Expression expression = expression();
        Token semicolon = consume(TokenType.SYMBOL, new String[]{";"});
        return new ExpressionStatement(
                expression,
                expression.getPosition(),
                semicolon.getPosition() + semicolon.getLength() - expression.getPosition()
        );
    }

    Expression expression() throws ParseError {
        return assignmentExpression();
    }

    Expression assignmentExpression() throws ParseError {
        assertLookAheadNotNull();

        Expression left = logicalExpression();

        if (lookahead == null || !lookahead.getValue().equals("=")) {
            return left;
        }

        Token operator = consume(TokenType.OPERATOR, new String[]{"="});

        assertLookAheadNotNull();

        Expression right = logicalExpression();

        int position = left.getPosition();
        int length = (right.getPosition() + right.getLength()) - left.getPosition();

        // TODO: Provide a dedicated class for assignment expression
        return new BinaryExpression(left, operator.getValue(), right, position, length, operator.getPosition(), operator.getLength());
    }

    Expression logicalExpression() throws ParseError {
        Expression left = equalityExpression();

        if (lookahead == null || !isLogicalOperator(lookahead.getValue())) {
            return left;
        }

        Token operator = consume(TokenType.OPERATOR, new String[]{"&&", "||"});

        assertLookAheadNotNull();

        Expression right = equalityExpression();

        int position = left.getPosition();
        int length = (right.getPosition() + right.getLength()) - left.getPosition();

        return new BinaryExpression(left, operator.getValue(), right, position, length, operator.getPosition(), operator.getLength());
    }

    private boolean isLogicalOperator(String operator) {
        return "&&".equals(operator) || "||".equals(operator);
    }

    Expression equalityExpression() throws ParseError {
        Expression left = relationalExpression();

        if (lookahead == null || !isEqualityOperator(lookahead.getValue())) {
            return left;
        }

        Token operator = consume(TokenType.OPERATOR, new String[]{"==", "!="});

        assertLookAheadNotNull();

        Expression right = equalityExpression();

        int position = left.getPosition();
        int length = (right.getPosition() + right.getLength()) - left.getPosition();

        return new BinaryExpression(left, operator.getValue(), right, position, length, operator.getPosition(), operator.getLength());
    }

    private boolean isEqualityOperator(String operator) {
        return "==".equals(operator) || "!=".equals(operator);
    }

    Expression relationalExpression() throws ParseError {
        Expression left = additiveExpression();

        if (lookahead == null || !isRelationalOperator(lookahead.getValue())) {
            return left;
        }

        Token operator = consume(TokenType.OPERATOR, new String[]{">=", "<=", ">", "<"});

        assertLookAheadNotNull();

        Expression right = relationalExpression();

        int position = left.getPosition();
        int length = (right.getPosition() + right.getLength()) - left.getPosition();

        return new BinaryExpression(left, operator.getValue(), right, position, length, operator.getPosition(), operator.getLength());
    }

    private boolean isRelationalOperator(String operator) {
        return ">".equals(operator) ||
                "<".equals(operator) ||
                ">=".equals(operator) ||
                "<=".equals(operator);
    }

    Expression additiveExpression() throws ParseError {
        Expression left = multiplicativeExpression();

        if (lookahead == null || !isAdditiveOperator(lookahead.getValue())) {
            return left;
        }

        Token operator = consume(TokenType.OPERATOR, new String[]{"+", "-"});

        assertLookAheadNotNull();

        Expression right = additiveExpression();

        int position = left.getPosition();
        int length = (right.getPosition() + right.getLength()) - left.getPosition();

        return new BinaryExpression(left, operator.getValue(), right, position, length, operator.getPosition(), operator.getLength());
    }

    private boolean isAdditiveOperator(String operator) {
        return "+".equals(operator) || "-".equals(operator);
    }

    Expression multiplicativeExpression() throws ParseError {
        Expression left = unaryExpression();

        if (lookahead == null || !isMultiplicativeOperator(lookahead.getValue())) {
            return left;
        }

        Token operator = consume(TokenType.OPERATOR, new String[]{"*", "/", "%"});

        assertLookAheadNotNull();

        Expression right = multiplicativeExpression();

        int position = left.getPosition();
        int length = (right.getPosition() + right.getLength()) - left.getPosition();

        return new BinaryExpression(left, operator.getValue(), right, position, length, operator.getPosition(), operator.getLength());
    }

    private boolean isMultiplicativeOperator(String operator) {
        return "*".equals(operator) || "/".equals(operator) || "%".equals(operator);
    }

    public Expression unaryExpression() throws ParseError {
        assertLookAheadNotNull();

        if (isUnaryOperator(lookahead.getValue())) {
            Token operator = consume(TokenType.OPERATOR, new String[]{"+", "-", "!"});
            Expression expression = primaryExpression();
            return new UnaryExpression(operator.getValue(), expression,
                    operator.getPosition(),
                    expression.getPosition() + expression.getLength() - operator.getPosition(),
                    operator.getPosition(),
                    operator.getLength());
        }

        return primaryExpression();
    }

    private boolean isUnaryOperator(String operator) {
        return "!+-".contains(operator);
    }

    Expression primaryExpression() throws ParseError {
        assertLookAheadNotNull();

        if (lookahead.getType() == TokenType.SYMBOL && lookahead.getValue().equals("(")) {
            consume(TokenType.SYMBOL, new String[]{"("});
            Expression expression = expression();
            consume(TokenType.SYMBOL, new String[]{")"});
            return expression;
        }

        if (lookahead.getType() == TokenType.IDENTIFIER) {
            Token token = consume(TokenType.IDENTIFIER);
            if (lookahead != null && lookahead.getValue().equals("(")) {
                // Function call
                consume(TokenType.SYMBOL, new String[]{"("});
                List<Expression> arguments = new ArrayList<>();
                assertLookAheadNotNull();
                while (!lookahead.getValue().equals(")")) {
                    arguments.add(expression());
                    if (lookahead.getValue().equals(",")) {
                        consume(TokenType.SYMBOL, new String[]{","});
                    }
                }
                Token close = consume(TokenType.SYMBOL, new String[]{")"});
                return new FunctionCallExpression(token.getValue(), arguments,
                        token.getPosition(),
                        close.getPosition() + close.getLength() - token.getPosition());
            }
            return new Identifier(token.getValue(), token.getPosition(), token.getLength());
        }

        if (lookahead.getType() == TokenType.NUMBER) {
            Token token = consume(TokenType.NUMBER);
            return new NumberLiteral(Double.parseDouble(token.getValue()), token.getPosition(), token.getLength());
        }

        if (lookahead.getType() == TokenType.STRING) {
            Token token = consume(TokenType.STRING);
            String value = token.getValue().substring(1, token.getLength() - 1);
            return new StringLiteral(value, token.getPosition(), token.getLength());
        }

        if (lookahead.getType() == TokenType.BOOLEAN) {
            Token token = consume(TokenType.BOOLEAN);
            return new BooleanLiteral(Boolean.parseBoolean(token.getValue()), token.getPosition(), token.getLength());
        }

        throw new ParseError("Expected a Number, String or Boolean literal", lookahead.getPosition(), lookahead.getLength());
    }

    Token consume(TokenType type) throws ParseError {
        assertLookAheadNotNull();

        if (lookahead.getType() != type) {
            throw new ParseError("Token type mismatch", lookahead.getPosition(), lookahead.getLength());
        }

        Token token = lookahead;
        tokenIdx++;
        if (tokenIdx >= tokens.size()) {
            lookahead = null;
        } else {
            lookahead = tokens.get(tokenIdx);
        }
        return token;
    }

    Token consume(TokenType type, String[] values) throws ParseError {
        if (lookahead == null) {
            Token last = tokens.get(tokens.size() - 1);
            throw new ParseError(
                    unexpectedTokenError(values, "End of stream reached unexpectedly"),
                    last.getPosition(),
                    last.getLength()
            );
        }

        if (lookahead.getType() != type) {
            throw new ParseError(
                    unexpectedTokenError(values, String.format("Unexpected '%s'", lookahead.getValue())),
                    lookahead.getPosition(),
                    lookahead.getLength()
            );
        }

        boolean found = Arrays.stream(values).anyMatch(v -> v.equals(lookahead.getValue()));
        if (!found) {
            throw new ParseError(
                    unexpectedTokenError(values, String.format("Unexpected '%s'", lookahead.getValue())),
                    lookahead.getPosition(),
                    lookahead.getLength()
            );
        }

        Token token = lookahead;
        tokenIdx++;
        if (tokenIdx >= tokens.size()) {
            lookahead = null;
        } else {
            lookahead = tokens.get(tokenIdx);
        }
        return token;
    }

    private String unexpectedTokenError(String[] values, String defaultMessage) {
        if (values == null || values.length == 0) {
            return defaultMessage;
        }

        if (values.length == 1) {
            return String.format("Expected a '%s'", values[0]);
        } else {
            return String.format("Expected one of %s", Arrays.stream(values).map(v -> "'" + v + "'").collect(Collectors.joining(",")));
        }
    }

    void assertLookAheadNotNull() throws ParseError {
        if (lookahead == null) {
            Token last = tokens.get(tokens.size() - 1);
            throw new ParseError("End of file reached unexpectedly", last.getPosition(), last.getLength());
        }
    }
}
