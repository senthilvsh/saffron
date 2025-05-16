package org.senthilvsh.saffron.stdlib.console;

import org.senthilvsh.saffron.ast.FunctionArgument;
import org.senthilvsh.saffron.common.Frame;
import org.senthilvsh.saffron.common.Type;
import org.senthilvsh.saffron.runtime.BooleanObj;
import org.senthilvsh.saffron.runtime.FunctionReturn;
import org.senthilvsh.saffron.stdlib.NativeFunction;

import java.util.List;

public class WriteBoolean implements NativeFunction {
    @Override
    public String getName() {
        return "write";
    }

    @Override
    public List<FunctionArgument> getArguments() {
        return List.of(new FunctionArgument("value", Type.BOOLEAN));
    }

    @Override
    public Type getReturnType() {
        return Type.VOID;
    }

    @Override
    public void run(Frame frame) throws FunctionReturn {
        BooleanObj baseObj = (BooleanObj) frame.get("value").getValue();
        System.out.print(baseObj.getValue());
    }
}
