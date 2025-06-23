package org.senthilvsh.saffron.stdlib.conversion;

import org.senthilvsh.saffron.runtime.ReturnStatementResult;
import org.senthilvsh.saffron.runtime.Scope;
import org.senthilvsh.saffron.runtime.StatementResult;
import org.senthilvsh.saffron.runtime.Variable;
import org.senthilvsh.saffron.stdlib.NativeFunction;

import java.util.List;

public class NumberToString implements NativeFunction {
    @Override
    public String getName() {
        return "to_str";
    }

    @Override
    public List<String> getArguments() {
        return List.of("source");
    }

    @Override
    public StatementResult run(Scope scope) {
        Variable sourceVar = scope.get("source");
        double source = (Double) sourceVar.getValue();

        String result;
        if ((long) source == source) {
            result = String.valueOf((long) source);
        } else {
            result = String.valueOf(source);
        }

        return new ReturnStatementResult(result);
    }
}
