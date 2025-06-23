package org.senthilvsh.saffron.stdlib.console;

import org.senthilvsh.saffron.runtime.ReturnStatementResult;
import org.senthilvsh.saffron.runtime.Scope;
import org.senthilvsh.saffron.runtime.StatementResult;
import org.senthilvsh.saffron.stdlib.NativeFunction;

import java.util.List;

public class WriteNL implements NativeFunction {
    @Override
    public String getName() {
        return "writeln";
    }

    @Override
    public List<String> getArguments() {
        return List.of("value");
    }

    @Override
    public StatementResult run(Scope scope) {
        Object baseObj = scope.get("value").getValue();
        System.out.println(baseObj.toString());
        return new ReturnStatementResult(null);
    }
}
