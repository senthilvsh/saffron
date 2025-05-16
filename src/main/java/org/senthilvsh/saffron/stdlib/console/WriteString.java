package org.senthilvsh.saffron.stdlib.console;

import org.senthilvsh.saffron.ast.FunctionArgument;
import org.senthilvsh.saffron.common.Frame;
import org.senthilvsh.saffron.common.Type;
import org.senthilvsh.saffron.runtime.FunctionReturn;
import org.senthilvsh.saffron.runtime.StringObj;
import org.senthilvsh.saffron.stdlib.NativeFunction;

import java.util.List;

public class WriteString implements NativeFunction {
    @Override
    public String getName() {
        return "write";
    }

    @Override
    public List<FunctionArgument> getArguments() {
        return List.of(new FunctionArgument("value", Type.STRING));
    }

    @Override
    public Type getReturnType() {
        return Type.VOID;
    }

    @Override
    public void run(Frame frame) throws FunctionReturn {
        StringObj baseObj = (StringObj) frame.get("value").getValue();
        System.out.print(baseObj.getValue());
    }
}
