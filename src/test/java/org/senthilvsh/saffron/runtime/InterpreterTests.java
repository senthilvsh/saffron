package org.senthilvsh.saffron.runtime;

import org.senthilvsh.saffron.ast.BooleanLiteral;
import org.senthilvsh.saffron.ast.Expression;
import org.senthilvsh.saffron.ast.NumberLiteral;
import org.senthilvsh.saffron.ast.StringLiteral;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class InterpreterTests {
    @Test
    public void testNumberLiteral() {
        NumberLiteral n = new NumberLiteral(10, 0, 0);
        Interpreter interpreter = new Interpreter();
        try {
            BaseObj value = interpreter.evaluate(n);
            Assertions.assertInstanceOf(NumberObj.class, value);
            NumberObj numberObj = (NumberObj) value;
            Assertions.assertEquals(10, numberObj.getValue());
            Assertions.assertEquals(Type.NUMBER, numberObj.getType());
        } catch (InterpreterException ex) {
            Assertions.fail("InterpreterException was thrown unexpectedly", ex);
        }
    }

    @Test
    public void testStringLiteral() {
        StringLiteral s = new StringLiteral("abcd", 0, 0);
        Interpreter interpreter = new Interpreter();
        try {
            BaseObj value = interpreter.evaluate(s);
            Assertions.assertInstanceOf(StringObj.class, value);
            StringObj stringObj = (StringObj) value;
            Assertions.assertEquals("abcd", stringObj.getValue());
            Assertions.assertEquals(Type.STRING, stringObj.getType());
        } catch (InterpreterException ex) {
            Assertions.fail("InterpreterException was thrown unexpectedly", ex);
        }
    }

    @Test
    public void testBooleanLiteral() {
        BooleanLiteral b = new BooleanLiteral(true, 0, 0);
        Interpreter interpreter = new Interpreter();
        try {
            BaseObj value = interpreter.evaluate(b);
            Assertions.assertInstanceOf(BooleanObj.class, value);
            BooleanObj booleanObj = (BooleanObj) value;
            Assertions.assertTrue(booleanObj.getValue());
            Assertions.assertEquals(Type.BOOLEAN, booleanObj.getType());
        } catch (InterpreterException ex) {
            Assertions.fail("InterpreterException was thrown unexpectedly", ex);
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
        Interpreter interpreter = new Interpreter();
        Assertions.assertThrows(InterpreterException.class, () -> interpreter.evaluate(unknown));
    }
}
