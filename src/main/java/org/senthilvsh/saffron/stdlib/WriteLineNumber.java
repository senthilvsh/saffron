package org.senthilvsh.saffron.stdlib;

import org.senthilvsh.saffron.ast.FunctionArgument;
import org.senthilvsh.saffron.common.Frame;
import org.senthilvsh.saffron.common.Type;
import org.senthilvsh.saffron.runtime.FunctionReturn;
import org.senthilvsh.saffron.runtime.NumberObj;

import java.util.List;

public class WriteLineNumber implements NativeFunction {
    @Override
    public String getName() {
        return "writeln";
    }

    @Override
    public List<FunctionArgument> getArguments() {
        return List.of(new FunctionArgument("value", Type.NUMBER));
    }

    @Override
    public Type getReturnType() {
        return Type.VOID;
    }

    @Override
    public void run(Frame frame) throws FunctionReturn {
        NumberObj numberObj = (NumberObj) frame.get("value").getValue();
        double value = numberObj.getValue();
        String str;
        if (value == (long) value) {
            str = String.valueOf((long) value);
        } else {
            str = String.valueOf(value);
        }
        System.out.println(str);
    }
}
