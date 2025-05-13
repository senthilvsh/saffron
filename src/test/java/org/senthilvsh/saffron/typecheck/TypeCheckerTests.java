package org.senthilvsh.saffron.typecheck;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.senthilvsh.saffron.ast.*;
import org.senthilvsh.saffron.runtime.Type;

public class TypeCheckerTests {
    @Test
    public void testNumberLiteral() {
        NumberLiteral n = new NumberLiteral(10, 0, 0);
        TypeChecker typeChecker = new TypeChecker();
        try {
            Type type = typeChecker.getType(n);
            Assertions.assertEquals(Type.NUMBER, type);
        } catch (TypeCheckerException ex) {
            Assertions.fail("TypeCheckerException was thrown unexpectedly", ex);
        }
    }

    @Test
    public void testStringLiteral() {
        StringLiteral s = new StringLiteral("abcd", 0, 0);
        TypeChecker typeChecker = new TypeChecker();
        try {
            Type type = typeChecker.getType(s);
            Assertions.assertEquals(Type.STRING, type);
        } catch (TypeCheckerException ex) {
            Assertions.fail("TypeCheckerException was thrown unexpectedly", ex);
        }
    }

    @Test
    public void testBooleanLiteral() {
        BooleanLiteral b = new BooleanLiteral(true, 0, 0);
        TypeChecker typeChecker = new TypeChecker();
        try {
            Type type = typeChecker.getType(b);
            Assertions.assertEquals(Type.BOOLEAN, type);
        } catch (TypeCheckerException ex) {
            Assertions.fail("TypeCheckerException was thrown unexpectedly", ex);
        }
    }

    @Test
    public void testUnknownLiteral() {
        Expression unknown = new Expression(0, 0) {
            @Override
            public int getPosition() {
                return super.getPosition();
            }

            @Override
            public int getLength() {
                return super.getLength();
            }
        };
        TypeChecker typeChecker = new TypeChecker();
        Assertions.assertThrows(TypeCheckerException.class, () -> typeChecker.getType(unknown));
    }

    @Test
    public void testBinaryExpressionAdditionBothNumbers() {
        Expression left = new NumberLiteral(10, 0, 2);
        Expression right = new NumberLiteral(20, 5, 2);
        Expression binaryExpression = new BinaryExpression(left, "+", right, 0, 7);
        TypeChecker typeChecker = new TypeChecker();
        try {
            Type type = typeChecker.getType(binaryExpression);
            Assertions.assertEquals(Type.NUMBER, type);
        } catch (TypeCheckerException e) {
            Assertions.fail("TypeCheckerException was thrown unexpectedly");
        }
    }

    @Test
    public void testBinaryExpressionAdditionOneString() {
        Expression left = new StringLiteral("10", 0, 4);
        Expression right = new NumberLiteral(20, 7, 2);
        Expression binaryExpression = new BinaryExpression(left, "+", right, 0, 9);
        TypeChecker typeChecker = new TypeChecker();
        try {
            Type type = typeChecker.getType(binaryExpression);
            Assertions.assertEquals(Type.STRING, type);
        } catch (TypeCheckerException e) {
            Assertions.fail("TypeCheckerException was thrown unexpectedly");
        }
    }
}
