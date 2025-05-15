package org.senthilvsh.saffron.ast;

public class Identifier extends Expression {
    private final String name;

    public Identifier(String name, int position, int length) {
        super(position, length);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return String.format("Identifier { name: %s; position: %d; length: %d }",
                name, getPosition(), getLength());
    }
}
