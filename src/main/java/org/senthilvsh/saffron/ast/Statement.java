package org.senthilvsh.saffron.ast;

public abstract class Statement {
    private final int position;
    private final int length;

    public Statement(int position, int length) {
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
