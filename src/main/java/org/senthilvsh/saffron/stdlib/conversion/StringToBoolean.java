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

public class StringToBoolean implements NativeFunction {
    @Override
    public String getName() {
        return "to_bool";
    }

    @Override
    public List<FunctionArgument> getArguments() {
        return List.of(
                new FunctionArgument("source", Type.STRING)
        );
    }

    @Override
    public Type getReturnType() {
        return Type.NUMBER;
    }

    @Override
    public void run(Frame frame) throws FunctionReturn {
        Variable sourceVar = frame.get("source");
        StringObj sourceObj = (StringObj) sourceVar.getValue();
        String source = sourceObj.getValue();

        boolean result;

        if ("true".equals(source)) {
            result = true;
        } else if ("false".equals(source)) {
            result = false;
        } else {
            throw new RuntimeException(String.format("Cannot convert '%s' into a boolean value", source));
        }

        BooleanObj returnObj = new BooleanObj(result);

        throw new FunctionReturn(returnObj);
    }
}
