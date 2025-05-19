package org.senthilvsh.saffron.ast;

public class TryCatchStatement extends Statement {
    private final Statement tryBlock;
    private final CatchBlockArgument exceptionType;
    private final CatchBlockArgument exceptionMessage;
    private final Statement catchBlock;

    public TryCatchStatement(Statement tryBlock, CatchBlockArgument exceptionType, CatchBlockArgument exceptionMessage,
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

    public CatchBlockArgument getExceptionType() {
        return exceptionType;
    }

    public CatchBlockArgument getExceptionMessage() {
        return exceptionMessage;
    }

    public Statement getCatchBlock() {
        return catchBlock;
    }
}
