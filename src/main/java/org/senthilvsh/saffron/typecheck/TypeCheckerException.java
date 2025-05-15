package org.senthilvsh.saffron.typecheck;

import org.senthilvsh.saffron.common.SaffronException;

public class TypeCheckerException extends SaffronException {
    public TypeCheckerException(String s, int position, int length) {
        super(s, position, length);
    }
}
