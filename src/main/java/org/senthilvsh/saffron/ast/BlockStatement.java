package org.senthilvsh.saffron.ast;

import java.util.List;

public class BlockStatement extends Statement {
    private final List<Statement> statements;

    public BlockStatement(List<Statement> statements, int position, int length) {
        super(position, length);
        this.statements = statements;
    }

    public List<Statement> getStatements() {
        return statements;
    }
}
