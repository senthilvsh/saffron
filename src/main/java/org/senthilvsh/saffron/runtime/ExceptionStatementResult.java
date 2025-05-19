package org.senthilvsh.saffron.runtime;

public class ExceptionStatementResult extends StatementResult {
    private final String exceptionType;
    private final String exceptionMessage;

    public ExceptionStatementResult(String exceptionType, String exceptionMessage) {
        super(StatementResultType.EXCEPTION);
        this.exceptionType = exceptionType;
        this.exceptionMessage = exceptionMessage;
    }

    public String getExceptionType() {
        return exceptionType;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }
}
