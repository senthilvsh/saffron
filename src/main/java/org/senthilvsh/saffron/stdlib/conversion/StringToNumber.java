package org.senthilvsh.saffron.stdlib.conversion;

import org.senthilvsh.saffron.runtime.*;
import org.senthilvsh.saffron.stdlib.NativeFunction;

import java.util.List;

public class StringToNumber implements NativeFunction {
    @Override
    public String getName() {
        return "to_num";
    }

    @Override
    public List<String> getArguments() {
        return List.of("source");
    }

    @Override
    public StatementResult run(Scope scope) throws NativeFunctionException {
        Variable sourceVar = scope.get("source");
        String source = (String) sourceVar.getValue();

        double result;
        try {
            result = Double.parseDouble(source);
        } catch (NumberFormatException e) {
            throw new NativeFunctionException("FORMAT_EXCEPTION", "Not a valid number");
        }

        return new ReturnStatementResult(result);
    }
}
