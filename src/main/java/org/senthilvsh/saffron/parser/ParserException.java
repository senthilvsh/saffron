package org.senthilvsh.saffron.parser;

public class ParserException extends Exception {
    private int position;
    private int length;

    public ParserException(String message) {
        super(message);
    }

    public ParserException(String message, int position, int length) {
        super(message);
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
