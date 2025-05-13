package org.senthilvsh.saffron.ast;

public abstract class Expression {
    private final int position;
    private final int length;

    public Expression(int position, int length) {
        this.position = position;
        this.length = length;
    }

    public int getPosition() {
        return position;
    }

    public int getLength() {
        return length;
    }
}
