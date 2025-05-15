package org.senthilvsh.saffron.parser;

import org.senthilvsh.saffron.common.SaffronException;

public class ParserException extends SaffronException {
    public ParserException(String s, int position, int length) {
        super(s, position, length);
    }
}
