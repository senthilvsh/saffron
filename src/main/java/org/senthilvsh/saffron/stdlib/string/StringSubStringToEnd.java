package org.senthilvsh.saffron.stdlib.string;

import org.senthilvsh.saffron.ast.FunctionArgument;
import org.senthilvsh.saffron.common.Frame;
import org.senthilvsh.saffron.common.Type;
import org.senthilvsh.saffron.common.Variable;
import org.senthilvsh.saffron.runtime.*;
import org.senthilvsh.saffron.stdlib.NativeFunction;

import java.util.List;

public class StringSubStringToEnd implements NativeFunction {
    @Override
    public String getName() {
        return "str_substr";
    }

    @Override
    public List<FunctionArgument> getArguments() {
        return List.of(
                new FunctionArgument("source", Type.STRING),
                new FunctionArgument("start", Type.NUMBER)
        );
    }

    @Override
    public Type getReturnType() {
        return Type.STRING;
    }

    @Override
    public StatementResult run(Frame frame) throws NativeFunctionException {
        Variable sourceVar = frame.get("source");
        StringObj sourceObj = (StringObj) sourceVar.getValue();
        String source = sourceObj.getValue();

        Variable startVar = frame.get("start");
        NumberObj startObj = (NumberObj) startVar.getValue();
        double start = startObj.getValue();

        if (start < 0) {
            throw new NativeFunctionException("INDEX_OUT_OF_BOUNDS_EXCEPTION", "Index out of bounds");
        }

        String result = source.substring((int) start);

        StringObj returnObj = new StringObj(result);

        return new ReturnStatementResult(returnObj);
    }
}
