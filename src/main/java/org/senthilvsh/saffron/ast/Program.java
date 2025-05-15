package org.senthilvsh.saffron.ast;

import java.util.List;

public class Program {
    private final List<Statement> statements;

    public Program(List<Statement> statements) {
        this.statements = statements;
    }

    public List<Statement> getStatements() {
        return statements;
    }
}
