package org.senthilvsh.saffron.ast;

public class TryCatchStatement extends Statement {
    private final Statement tryBlock;
    private final String exceptionType;
    private final String exceptionMessage;
    private final Statement catchBlock;

    public TryCatchStatement(Statement tryBlock, String exceptionType, String exceptionMessage,
                             Statement catchBlock, int position, int length) {
        super(position, length);
        this.tryBlock = tryBlock;
        this.exceptionType = exceptionType;
        this.exceptionMessage = exceptionMessage;
        this.catchBlock = catchBlock;
    }

    public Statement getTryBlock() {
        return tryBlock;
    }

    public String getExceptionType() {
        return exceptionType;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public Statement getCatchBlock() {
        return catchBlock;
    }
}
