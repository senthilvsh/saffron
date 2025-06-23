package org.senthilvsh.saffron.stdlib.string;

import org.senthilvsh.saffron.runtime.ReturnStatementResult;
import org.senthilvsh.saffron.runtime.Scope;
import org.senthilvsh.saffron.runtime.StatementResult;
import org.senthilvsh.saffron.runtime.Variable;
import org.senthilvsh.saffron.stdlib.NativeFunction;

import java.util.List;

public class StringEndsWith implements NativeFunction {
    @Override
    public String getName() {
        return "str_endswith";
    }

    @Override
    public List<String> getArguments() {
        return List.of("source", "search");
    }

    @Override
    public StatementResult run(Scope scope) {
        Variable sourceVar = scope.get("source");
        String source = (String) sourceVar.getValue();

        Variable searchVar = scope.get("search");
        String search = (String) searchVar.getValue();

        boolean result = source.endsWith(search);

        return new ReturnStatementResult(result);
    }
}
