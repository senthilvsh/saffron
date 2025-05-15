package org.senthilvsh.saffron.runtime;

import org.senthilvsh.saffron.common.SaffronException;

public class InterpreterException extends SaffronException {
    public InterpreterException(String s, int position, int length) {
        super(s, position, length);
    }
}
