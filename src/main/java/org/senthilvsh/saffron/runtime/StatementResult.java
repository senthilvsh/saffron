package org.senthilvsh.saffron.runtime;

/**
 * Represents the result of executing a statement.
 */
public class StatementResult {
    private final StatementResultType type;

    public StatementResult(StatementResultType type) {
        this.type = type;
    }

    public StatementResultType getType() {
        return type;
    }
}
