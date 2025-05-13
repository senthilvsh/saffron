package org.senthilvsh.saffron.ast;

public class NumberLiteral extends Expression {
    private final double value;

    public NumberLiteral(double value, int position, int length) {
        super(position, length);
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.format("NumberLiteral { value: %f; position: %d; length: %d }",
                value, getPosition(), getLength());
    }
}
