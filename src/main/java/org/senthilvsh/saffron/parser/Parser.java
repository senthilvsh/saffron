package org.senthilvsh.saffron.parser;

import org.senthilvsh.saffron.ast.*;

public class Parser {
    private final Lexer lexer;
    private Token lookahead;

    public Parser(String source) {
        lexer = new Lexer(source);
        lookahead = lexer.next();
    }

    Expression additiveExpression() throws ParserException {
        Expression left = primaryExpression();

        if (lookahead == null) {
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

        return new BinaryExpression(left, operator.getValue(), right, position, length);
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
            throw new ParserException("Reached end of stream unexpectedly");
        }

        if (lookahead.getType() != type) {
            throw new ParserException("Token type mismatch", lookahead.getPosition(), lookahead.getLength());
        }

        Token token = lookahead;
        lookahead = lexer.next();
        return token;
    }
}
