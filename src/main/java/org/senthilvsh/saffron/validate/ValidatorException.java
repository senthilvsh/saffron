package org.senthilvsh.saffron.validate;

import org.senthilvsh.saffron.common.SaffronException;

public class ValidatorException extends SaffronException {
    public ValidatorException(String s, int position, int length) {
        super(s, position, length);
    }
}
