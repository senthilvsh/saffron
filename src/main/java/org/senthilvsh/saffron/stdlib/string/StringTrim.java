package org.senthilvsh.saffron.stdlib.string;

import org.senthilvsh.saffron.ast.FunctionArgument;
import org.senthilvsh.saffron.common.Scope;
import org.senthilvsh.saffron.common.Type;
import org.senthilvsh.saffron.common.Variable;
import org.senthilvsh.saffron.runtime.ReturnStatementResult;
import org.senthilvsh.saffron.runtime.StatementResult;
import org.senthilvsh.saffron.runtime.StringObj;
import org.senthilvsh.saffron.stdlib.NativeFunction;

import java.util.List;

public class StringTrim implements NativeFunction {
    @Override
    public String getName() {
        return "str_trim";
    }

    @Override
    public List<FunctionArgument> getArguments() {
        return List.of(
                new FunctionArgument("source", Type.STRING)
        );
    }

    @Override
    public Type getReturnType() {
        return Type.STRING;
    }

    @Override
    public StatementResult run(Scope scope) {
        Variable sourceVar = scope.get("source");
        StringObj sourceObj = (StringObj) sourceVar.getValue();
        String source = sourceObj.getValue();

        String result = source.trim();

        StringObj returnObj = new StringObj(result);

        return new ReturnStatementResult(returnObj);
    }
}
