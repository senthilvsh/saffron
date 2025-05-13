package org.senthilvsh.saffron.ast;

public class StringLiteral extends Expression {
    private final String value;

    public StringLiteral(String value, int position, int length) {
        super(position, length);
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.format("StringLiteral { value: %s; position: %d; length: %d }",
                value, getPosition(), getLength());
    }
}
