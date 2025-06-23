package org.senthilvsh.saffron.runtime;

public class RuntimeError extends SaffronException {
    public RuntimeError(String s, int position, int length) {
        super(s, position, length);
    }
}
