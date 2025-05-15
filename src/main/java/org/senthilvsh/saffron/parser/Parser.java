package org.senthilvsh.saffron.parser;

import org.senthilvsh.saffron.ast.*;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    private final Lexer lexer;
    private Token lookahead;

    public Parser(String source) {
        lexer = new Lexer(source);
        lookahead = lexer.next();
        // TODO: Get all tokens at once into a list and start parsing. This will help with error position at EOF
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

        Token semicolon = consume(TokenType.SYMBOL, ";");

        return new ExpressionStatement(expression, expression.getPosition(), semicolon.getPosition() + semicolon.getLength() - expression.getPosition());
    }

    Expression additiveExpression() throws ParserException {
        Expression left = primaryExpression();

        if (lookahead == null || !(lookahead.getValue().equals("+") || lookahead.getValue().equals("-"))) {
            return left;
        }

        Token operator = consume(TokenType.OPERATOR);
        if (!operator.getValue().equals("+") && !operator.getValue().equals("-")) {
            throw new ParserException("Expected a '+' or '-'", operator.getPosition(), operator.getLength());
        }

        if (lookahead == null) {
            throw new ParserException("Reached end of stream unexpectedly");
        }

        Expression right = additiveExpression();

        int position = left.getPosition();
        int length = (right.getPosition() + right.getLength()) - left.getPosition();

        return new BinaryExpression(left, operator.getValue(), right, position, length, operator.getPosition(), operator.getLength());
    }

    Expression primaryExpression() throws ParserException {
        if (lookahead == null) {
            throw new ParserException("Reached end of stream unexpectedly");
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

        throw new ParserException("Expected a Number, String or Boolean literal", lookahead.getPosition(), lookahead.getLength());
    }

    Token consume(TokenType type) throws ParserException {
        if (lookahead == null) {
            throw new ParserException("Reached end of file unexpectedly");
        }

        // TODO: In case of invalid token, throw specific exception

        if (lookahead.getType() != type) {
            throw new ParserException("Token type mismatch", lookahead.getPosition(), lookahead.getLength());
        }

        Token token = lookahead;
        lookahead = lexer.next();
        return token;
    }

    Token consume(TokenType type, String value) throws ParserException {
        if (lookahead == null) {
            throw new ParserException(String.format("Expected a '%s' but reached end of file", value));
        }

        // TODO: In case of invalid token, throw specific exception

        if (lookahead.getType() != type) {
            throw new ParserException("Token type mismatch", lookahead.getPosition(), lookahead.getLength());
        }

        if (!lookahead.getValue().equals(value)) {
            throw new ParserException(String.format("Expected a '%s'", value), lookahead.getPosition(), lookahead.getLength());
        }

        Token token = lookahead;
        lookahead = lexer.next();
        return token;
    }
}
