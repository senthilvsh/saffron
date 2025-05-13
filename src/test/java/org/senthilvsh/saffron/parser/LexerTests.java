package org.senthilvsh.saffron.parser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LexerTests {
    @Test
    public void testEof() {
        Lexer lexer = new Lexer("");
        Assertions.assertNull(lexer.next());
    }

    @Test
    public void testWhitespace() {
        Lexer lexer = new Lexer("  \n  \r\n  \t  ");
        Assertions.assertNull(lexer.next());
    }

    @Test
    public void testNumber() {
        Lexer lexer = new Lexer("  12.34  ");
        Token token = lexer.next();
        Assertions.assertEquals(TokenType.NUMBER, token.getType());
        Assertions.assertEquals("12.34", token.getValue());
        Assertions.assertEquals(2, token.getPosition());
        Assertions.assertEquals(5, token.getLength());
    }

    @Test
    public void testString() {
        Lexer lexer = new Lexer("  \"abcd\"  ");
        Token token = lexer.next();
        Assertions.assertEquals(TokenType.STRING, token.getType());
        Assertions.assertEquals("\"abcd\"", token.getValue());
        Assertions.assertEquals(2, token.getPosition());
        Assertions.assertEquals(6, token.getLength());
    }

    @Test
    public void testBoolean() {
        Lexer lexer = new Lexer("  true  ");
        Token token = lexer.next();
        Assertions.assertEquals(TokenType.BOOLEAN, token.getType());
        Assertions.assertEquals("true", token.getValue());
        Assertions.assertEquals(2, token.getPosition());
        Assertions.assertEquals(4, token.getLength());
    }

    @Test
    public void testOperator() {
        Lexer lexer = new Lexer("  +  ");
        Token token = lexer.next();
        Assertions.assertEquals(TokenType.OPERATOR, token.getType());
        Assertions.assertEquals("+", token.getValue());
        Assertions.assertEquals(2, token.getPosition());
        Assertions.assertEquals(1, token.getLength());
    }

    @Test
    public void testUnknown() {
        Lexer lexer = new Lexer("  $  ");
        Token token = lexer.next();
        Assertions.assertEquals(TokenType.UNKNOWN, token.getType());
        Assertions.assertEquals("$", token.getValue());
        Assertions.assertEquals(2, token.getPosition());
        Assertions.assertEquals(1, token.getLength());
    }
}
