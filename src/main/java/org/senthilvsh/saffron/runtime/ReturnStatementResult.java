package org.senthilvsh.saffron.runtime;

public class ReturnStatementResult extends StatementResult {
    private final Object returnValue;

    public ReturnStatementResult(Object returnValue) {
        super(StatementResultType.RETURN);
        this.returnValue = returnValue;
    }

    public Object getReturnValue() {
        return returnValue;
    }
}
