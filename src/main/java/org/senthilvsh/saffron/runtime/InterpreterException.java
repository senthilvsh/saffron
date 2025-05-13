package org.senthilvsh.saffron.runtime;

public class InterpreterException extends Exception {
    private final int position;
    private final int length;

    public InterpreterException(String s, int position, int length) {
        super(s);
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
