package org.senthilvsh.saffron.parser;

import org.senthilvsh.saffron.ast.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Parser {

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

    // TODO: Return a ParserResult which includes a list of errors along with the program

    public Program program() throws ParserException {
        List<Statement> statements = new ArrayList<>();
        while (lookahead != null) {
            statements.add(statement());
        }
        return new Program(statements);
    }

    Statement statement() throws ParserException {
        // TODO: In case of invalid token or mismatch, create error statement and continue parsing from the next statement
        Expression expression = additiveExpression();

        Token semicolon = consume(TokenType.SYMBOL, new String[]{";"});

        return new ExpressionStatement(expression, expression.getPosition(), semicolon.getPosition() + semicolon.getLength() - expression.getPosition());
    }

    Expression additiveExpression() throws ParserException {
        Expression left = primaryExpression();

        if (lookahead == null || !(lookahead.getValue().equals("+") || lookahead.getValue().equals("-"))) {
            return left;
        }

        Token operator = consume(TokenType.OPERATOR, new String[]{"+", "-"});

        assertLookAheadNotNull();

        Expression right = additiveExpression();

        int position = left.getPosition();
        int length = (right.getPosition() + right.getLength()) - left.getPosition();

        return new BinaryExpression(left, operator.getValue(), right, position, length, operator.getPosition(), operator.getLength());
    }

    Expression primaryExpression() throws ParserException {
        assertLookAheadNotNull();

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

        throw new ParserException("Expected a Number, String or Boolean literal", lookahead.getPosition(), lookahead.getLength());
    }

    Token consume(TokenType type) throws ParserException {
        assertLookAheadNotNull();

        // TODO: In case of invalid token, throw specific exception

        if (lookahead.getType() != type) {
            throw new ParserException("Token type mismatch", lookahead.getPosition(), lookahead.getLength());
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

    Token consume(TokenType type, String[] values) throws ParserException {
        if (lookahead == null) {
            Token last = tokens.get(tokens.size() - 1);
            throw new ParserException(String.format("Expected one of %s", Arrays.stream(values).map(v -> "'" + v + "'").collect(Collectors.joining(","))),
                    last.getPosition(), last.getLength());
        }

        // TODO: In case of invalid token, throw specific exception

        if (lookahead.getType() != type) {
            throw new ParserException(String.format("Expected one of %s", Arrays.stream(values).map(v -> "'" + v + "'").collect(Collectors.joining(","))), lookahead.getPosition(), lookahead.getLength());
        }

        boolean found = false;
        for (String value : values) {
            if (lookahead.getValue().equals(value)) {
                found = true;
                break;
            }
        }
        if (!found) {
            throw new ParserException(String.format("Expected one of %s", Arrays.stream(values).map(v -> "'" + v + "'").collect(Collectors.joining(","))), lookahead.getPosition(), lookahead.getLength());
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

    void assertLookAheadNotNull() throws ParserException {
        if (lookahead == null) {
            Token last = tokens.get(tokens.size() - 1);
            throw new ParserException("End of file reached unexpectedly", last.getPosition(), last.getLength());
        }
    }
}
