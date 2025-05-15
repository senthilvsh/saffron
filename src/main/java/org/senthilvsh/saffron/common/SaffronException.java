package org.senthilvsh.saffron.common;

public class SaffronException extends Exception {
    private final int position;
    private final int length;

    public SaffronException(String s, int position, int length) {
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
