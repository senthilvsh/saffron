package org.senthilvsh.saffron.stdlib;

import org.senthilvsh.saffron.ast.FunctionArgument;
import org.senthilvsh.saffron.common.Frame;
import org.senthilvsh.saffron.common.Type;
import org.senthilvsh.saffron.runtime.BooleanObj;
import org.senthilvsh.saffron.runtime.FunctionReturn;

import java.util.List;

public class WriteLineBoolean implements NativeFunction {
    @Override
    public String getName() {
        return "writeln";
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
        System.out.println(baseObj.getValue());
    }
}
