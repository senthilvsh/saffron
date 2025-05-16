package org.senthilvsh.saffron.runtime;

import org.senthilvsh.saffron.common.SaffronException;

public class RuntimeError extends SaffronException {
    public RuntimeError(String s, int position, int length) {
        super(s, position, length);
    }
}
