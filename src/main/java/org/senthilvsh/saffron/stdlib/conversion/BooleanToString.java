package org.senthilvsh.saffron.stdlib.conversion;

import org.senthilvsh.saffron.ast.FunctionArgument;
import org.senthilvsh.saffron.common.Frame;
import org.senthilvsh.saffron.common.Type;
import org.senthilvsh.saffron.common.Variable;
import org.senthilvsh.saffron.runtime.BooleanObj;
import org.senthilvsh.saffron.runtime.FunctionReturn;
import org.senthilvsh.saffron.runtime.NumberObj;
import org.senthilvsh.saffron.runtime.StringObj;
import org.senthilvsh.saffron.stdlib.NativeFunction;

import java.util.List;

public class BooleanToString implements NativeFunction {
    @Override
    public String getName() {
        return "to_str";
    }

    @Override
    public List<FunctionArgument> getArguments() {
        return List.of(
                new FunctionArgument("source", Type.BOOLEAN)
        );
    }

    @Override
    public Type getReturnType() {
        return Type.STRING;
    }

    @Override
    public void run(Frame frame) throws FunctionReturn {
        Variable sourceVar = frame.get("source");
        BooleanObj sourceObj = (BooleanObj) sourceVar.getValue();
        boolean source = sourceObj.getValue();

        String result = String.valueOf(source);

        StringObj returnObj = new StringObj(result);

        throw new FunctionReturn(returnObj);
    }
}
