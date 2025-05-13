package org.senthilvsh.saffron.parser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.senthilvsh.saffron.ast.*;

public class ParserTests {
    @Test
    public void testConsumeEof() {
        Parser parser = new Parser("  ");
        Assertions.assertThrows(ParserException.class, () -> parser.consume(TokenType.NUMBER));
    }

    @Test
    public void testConsume() {
        Parser parser = new Parser("  12.34  ");
        try {
            Token token = parser.consume(TokenType.NUMBER);
            Assertions.assertEquals("12.34", token.getValue());
            Assertions.assertEquals(2, token.getPosition());
            Assertions.assertEquals(5, token.getLength());
        } catch (ParserException e) {
            Assertions.fail("ParserError thrown unexpectedly");
        }
    }

    @Test
    public void testConsumeWrongTokenType() {
        Parser parser = new Parser("  12.34  ");
        ParserException ex = Assertions.assertThrows(ParserException.class, () -> parser.consume(TokenType.STRING));
        Assertions.assertEquals(2, ex.getPosition());
        Assertions.assertEquals(5, ex.getLength());
    }

    @Test
    public void testPrimaryExpressionNumber() {
        Parser parser = new Parser("  12.34  ");
        try {
            Expression expression = parser.primaryExpression();
            Assertions.assertInstanceOf(NumberLiteral.class, expression);
            NumberLiteral numberLiteral = (NumberLiteral) expression;
            Assertions.assertEquals(12.34, numberLiteral.getValue());
            Assertions.assertEquals(2, numberLiteral.getPosition());
            Assertions.assertEquals(5, numberLiteral.getLength());
        } catch (ParserException ex) {
            Assertions.fail("ParserError thrown unexpectedly");
        }
    }

    @Test
    public void testPrimaryExpressionString() {
        Parser parser = new Parser("  \"abcd\"  ");
        try {
            Expression expression = parser.primaryExpression();
            Assertions.assertInstanceOf(StringLiteral.class, expression);
            StringLiteral stringLiteral = (StringLiteral) expression;
            Assertions.assertEquals("abcd", stringLiteral.getValue());
            Assertions.assertEquals(2, stringLiteral.getPosition());
            Assertions.assertEquals(6, stringLiteral.getLength());
        } catch (ParserException ex) {
            Assertions.fail("ParserError thrown unexpectedly");
        }
    }

    @Test
    public void testPrimaryExpressionBoolean() {
        Parser parser = new Parser("  true  ");
        try {
            Expression expression = parser.primaryExpression();
            Assertions.assertInstanceOf(BooleanLiteral.class, expression);
            BooleanLiteral booleanLiteral = (BooleanLiteral) expression;
            Assertions.assertTrue(booleanLiteral.getValue());
            Assertions.assertEquals(2, booleanLiteral.getPosition());
            Assertions.assertEquals(4, booleanLiteral.getLength());
        } catch (ParserException ex) {
            Assertions.fail("ParserError thrown unexpectedly");
        }
    }

    @Test
    public void testPrimaryExpressionEof() {
        Parser parser = new Parser("  ");
        Assertions.assertThrows(ParserException.class, parser::primaryExpression);
    }

    @Test
    public void testPrimaryExpressionInvalid() {
        Parser parser = new Parser("  $  ");
        ParserException ex = Assertions.assertThrows(ParserException.class, parser::primaryExpression);
        Assertions.assertEquals(2, ex.getPosition());
        Assertions.assertEquals(1, ex.getLength());
    }

    @Test
    public void testAdditiveExpression() {
        Parser parser = new Parser("  10 + 20  ");
        try {
            Expression expression = parser.additiveExpression();
            Assertions.assertInstanceOf(BinaryExpression.class, expression);
            BinaryExpression binaryExpression = (BinaryExpression) expression;
            Assertions.assertEquals("+", binaryExpression.getOperator());
        } catch (ParserException ex) {
            Assertions.fail("ParserError thrown unexpectedly");
        }
    }

    @Test
    public void testAdditiveExpressionLeftOnly() {
        Parser parser = new Parser("  10  ");
        try {
            Expression expression = parser.additiveExpression();
            Assertions.assertInstanceOf(NumberLiteral.class, expression);
        } catch (ParserException ex) {
            Assertions.fail("ParserError thrown unexpectedly");
        }
    }

    @Test
    public void testAdditiveExpressionIncomplete() {
        Parser parser = new Parser("  10 + ");
        Assertions.assertThrows(ParserException.class, parser::additiveExpression);
    }
}
