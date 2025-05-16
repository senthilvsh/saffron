package org.senthilvsh.saffron.validate;

import org.senthilvsh.saffron.common.SaffronException;

public class ValidationError extends SaffronException {
    public ValidationError(String s, int position, int length) {
        super(s, position, length);
    }
}
