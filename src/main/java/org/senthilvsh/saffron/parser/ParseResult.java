package org.senthilvsh.saffron.parser;

import org.senthilvsh.saffron.ast.Program;

import java.util.List;

public class ParseResult {
    private final Program program;
    private final List<ParseError> errors;

    public ParseResult(Program program, List<ParseError> errors) {
        this.program = program;
        this.errors = errors;
    }

    public Program getProgram() {
        return program;
    }

    public List<ParseError> getErrors() {
        return errors;
    }
}
