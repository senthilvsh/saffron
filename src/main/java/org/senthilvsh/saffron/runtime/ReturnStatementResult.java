package org.senthilvsh.saffron.runtime;

public class ReturnStatementResult extends StatementResult {
    private final BaseObj returnValue;

    public ReturnStatementResult(BaseObj returnValue) {
        super(StatementResultType.RETURN);
        this.returnValue = returnValue;
    }

    public BaseObj getReturnValue() {
        return returnValue;
    }
}
