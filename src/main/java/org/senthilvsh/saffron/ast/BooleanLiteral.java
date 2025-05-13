package org.senthilvsh.saffron.ast;

public class BooleanLiteral extends Expression {
    private final boolean value;

    public BooleanLiteral(boolean value, int position, int length) {
        super(position, length);
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.format("BooleanLiteral { value: %b; position: %d; length: %d }",
                value, getPosition(), getLength());
    }
}
