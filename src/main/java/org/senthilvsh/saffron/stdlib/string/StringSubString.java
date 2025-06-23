package org.senthilvsh.saffron.stdlib.string;

import org.senthilvsh.saffron.runtime.*;
import org.senthilvsh.saffron.stdlib.NativeFunction;

import java.util.List;

public class StringSubString implements NativeFunction {
    @Override
    public String getName() {
        return "str_substr";
    }

    @Override
    public List<String> getArguments() {
        return List.of("source", "start", "end");
    }

    @Override
    public StatementResult run(Scope scope) throws NativeFunctionException {
        Variable sourceVar = scope.get("source");
        String source = (String) sourceVar.getValue();

        Variable startVar = scope.get("start");
        double start = (Double) startVar.getValue();

        Variable endVar = scope.get("end");
        double end = (Double) endVar.getValue();

        if (start < 0 || end >= source.length()) {
            throw new NativeFunctionException("INDEX_OUT_OF_BOUNDS_EXCEPTION", "Index out of bounds");
        }

        String result = source.substring((int) start, (int) end + 1);

        return new ReturnStatementResult(result);
    }
}
