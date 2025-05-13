package org.senthilvsh.saffron.runtime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TypeTests {
    @Test
    public void testNumberType() {
        NumberObj n = new NumberObj(10);
        Assertions.assertEquals(Type.NUMBER, n.getType());
    }

    @Test
    public void testStringType() {
        StringObj s = new StringObj("abcd");
        Assertions.assertEquals(Type.STRING, s.getType());
    }

    @Test
    public void testBooleanType() {
        BooleanObj b = new BooleanObj(true);
        Assertions.assertEquals(Type.BOOLEAN, b.getType());
    }
}
