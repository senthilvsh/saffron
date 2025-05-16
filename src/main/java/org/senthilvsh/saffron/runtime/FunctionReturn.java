package org.senthilvsh.saffron.runtime;

public class FunctionReturn extends RuntimeException {
    private final BaseObj returnValue;

    public FunctionReturn(BaseObj returnValue) {
        this.returnValue = returnValue;
    }

    public BaseObj getReturnValue() {
        return returnValue;
    }
}
