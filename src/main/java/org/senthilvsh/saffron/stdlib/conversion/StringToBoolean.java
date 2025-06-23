package org.senthilvsh.saffron.stdlib.conversion;

import org.senthilvsh.saffron.runtime.*;
import org.senthilvsh.saffron.stdlib.NativeFunction;

import java.util.List;

public class StringToBoolean implements NativeFunction {
    @Override
    public String getName() {
        return "to_bool";
    }

    @Override
    public List<String> getArguments() {
        return List.of("source");
    }

    @Override
    public StatementResult run(Scope scope) throws NativeFunctionException {
        Variable sourceVar = scope.get("source");
        String source = (String) sourceVar.getValue();

        boolean result;

        if ("true".equals(source)) {
            result = true;
        } else if ("false".equals(source)) {
            result = false;
        } else {
            throw new NativeFunctionException("FORMAT_EXCEPTION", "Not a valid boolean");
        }

        return new ReturnStatementResult(result);
    }
}
