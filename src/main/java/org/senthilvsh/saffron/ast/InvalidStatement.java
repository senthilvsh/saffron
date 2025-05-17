package org.senthilvsh.saffron.ast;

// TODO: Maybe return null instead of this class?
public class InvalidStatement extends Statement {
    public InvalidStatement(int position, int length) {
        super(position, length);
    }
}
