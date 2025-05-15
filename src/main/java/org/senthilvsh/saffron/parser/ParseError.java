package org.senthilvsh.saffron.parser;

import org.senthilvsh.saffron.common.SaffronException;

public class ParseError extends SaffronException {
    public ParseError(String s, int position, int length) {
        super(s, position, length);
    }
}
