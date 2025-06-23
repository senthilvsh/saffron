package org.senthilvsh.saffron.stdlib.string;

import org.senthilvsh.saffron.runtime.ReturnStatementResult;
import org.senthilvsh.saffron.runtime.Scope;
import org.senthilvsh.saffron.runtime.StatementResult;
import org.senthilvsh.saffron.runtime.Variable;
import org.senthilvsh.saffron.stdlib.NativeFunction;

import java.util.List;

public class StringReplace implements NativeFunction {
    @Override
    public String getName() {
        return "str_replace";
    }

    @Override
    public List<String> getArguments() {
        return List.of("source", "search", "replace");
    }

    @Override
    public StatementResult run(Scope scope) {
        Variable sourceVar = scope.get("source");
        String source = (String) sourceVar.getValue();

        Variable searchVar = scope.get("search");
        String search = (String) searchVar.getValue();

        Variable replaceVar = scope.get("replace");
        String replace = (String) replaceVar.getValue();

        String result = source.replace(search, replace);

        return new ReturnStatementResult(result);
    }
}
