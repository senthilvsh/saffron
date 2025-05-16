package org.senthilvsh.saffron.parser;

import org.senthilvsh.saffron.ast.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Parser {
    private final List<ParseError> errors = new ArrayList<>();
    private final List<Token> tokens = new ArrayList<>();
    private int tokenIdx = 0;
    private Token lookahead;

    public Parser(String source) {
        Lexer lexer = new Lexer(source);

        Token token = lexer.next();
        while (token != null) {
            tokens.add(token);
            token = lexer.next();
        }

        if (!tokens.isEmpty()) {
            lookahead = tokens.get(0);
        }
    }

    public ParseResult parse() throws ParseError {
        List<Statement> statements = new ArrayList<>();
        while (lookahead != null) {
            try {
                statements.add(statement());
            } catch (ParseError e) {
                errors.add(e);
                while (!lookahead.getValue().equals(";")) {
                    consume();
                }
                consume(TokenType.SYMBOL, new String[]{";"});
            }
        }
        return new ParseResult(new Program(statements), errors);
    }

    Statement statement() throws ParseError {
        assertLookAheadNotNull();

        if (lookahead.getType() == TokenType.KEYWORD && lookahead.getValue().equals("var")) {
            Token varKeyword = consume(TokenType.KEYWORD, new String[]{"var"});
            Token variableName = consume(TokenType.IDENTIFIER);
            consume(TokenType.SYMBOL, new String[]{":"});
            Token typeSpecifier = consume(TokenType.KEYWORD, new String[]{"num", "str", "bool"});
            Token semicolon = consume(TokenType.SYMBOL, new String[]{";"});
            return new VariableDeclaration(variableName.getValue(), typeSpecifier.getValue(),
                    varKeyword.getPosition(), semicolon.getPosition() + semicolon.getLength() - varKeyword.getPosition());
        }

        if (lookahead.getType() == TokenType.KEYWORD && lookahead.getValue().equals("print")) {
            Token printKeyword = consume(TokenType.KEYWORD, new String[]{"print"});
            Expression expression = expression();
            Token semicolon = consume(TokenType.SYMBOL, new String[]{";"});
            return new PrintStatement(expression, printKeyword.getPosition(),
                    semicolon.getPosition() + semicolon.getLength() - printKeyword.getPosition());
        }

        if (lookahead.getType() == TokenType.SYMBOL && lookahead.getValue().equals("{")) {
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

        Expression expression = expression();
        Token semicolon = consume(TokenType.SYMBOL, new String[]{";"});
        return new ExpressionStatement(expression, expression.getPosition(),
                semicolon.getPosition() + semicolon.getLength() - expression.getPosition());
    }

    Expression expression() throws ParseError {
        return assignmentExpression();
    }

    Expression assignmentExpression() throws ParseError {
        assertLookAheadNotNull();

        Expression left = equalityExpression();

        if (lookahead == null || !lookahead.getValue().equals("=")) {
            return left;
        }

        Token operator = consume(TokenType.OPERATOR, new String[]{"="});

        assertLookAheadNotNull();

        Expression right = equalityExpression();

        int position = left.getPosition();
        int length = (right.getPosition() + right.getLength()) - left.getPosition();

        // TODO: Dedicated class for assignment expression
        return new BinaryExpression(left, operator.getValue(), right, position, length, operator.getPosition(), operator.getLength());
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

    void consume() throws ParseError {
        assertLookAheadNotNull();
        tokenIdx++;
        if (tokenIdx >= tokens.size()) {
            lookahead = null;
        } else {
            lookahead = tokens.get(tokenIdx);
        }
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
            throw new ParseError(String.format("Expected one of %s",
                    Arrays.stream(values).map(v -> "'" + v + "'").collect(Collectors.joining(","))),
                    last.getPosition(), last.getLength());
        }

        if (lookahead.getType() != type) {
            throw new ParseError(String.format("Expected one of %s",
                    Arrays.stream(values).map(v -> "'" + v + "'").collect(Collectors.joining(","))),
                    lookahead.getPosition(), lookahead.getLength());
        }

        boolean found = false;
        for (String value : values) {
            if (lookahead.getValue().equals(value)) {
                found = true;
                break;
            }
        }
        if (!found) {
            // TODO: Better error message when only one item is in values array
            throw new ParseError(String.format("Expected one of %s",
                    Arrays.stream(values).map(v -> "'" + v + "'").collect(Collectors.joining(","))),
                    lookahead.getPosition(), lookahead.getLength());
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

    void assertLookAheadNotNull() throws ParseError {
        if (lookahead == null) {
            Token last = tokens.get(tokens.size() - 1);
            throw new ParseError("End of file reached unexpectedly", last.getPosition(), last.getLength());
        }
    }
}
