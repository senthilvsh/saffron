package org.senthilvsh.saffron.stdlib.conversion;

import org.senthilvsh.saffron.ast.FunctionArgument;
import org.senthilvsh.saffron.common.Frame;
import org.senthilvsh.saffron.common.Type;
import org.senthilvsh.saffron.common.Variable;
import org.senthilvsh.saffron.runtime.*;
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
    public StatementResult run(Frame frame) {
        Variable sourceVar = frame.get("source");
        StringObj sourceObj = (StringObj) sourceVar.getValue();
        String source = sourceObj.getValue();

        boolean result;

        if ("true".equals(source)) {
            result = true;
        } else if ("false".equals(source)) {
            result = false;
        } else {
            return new ExceptionStatementResult("FORMAT_EXCEPTION", "Not a valid boolean");
        }

        BooleanObj returnObj = new BooleanObj(result);

        return new ReturnStatementResult(returnObj);
    }
}
