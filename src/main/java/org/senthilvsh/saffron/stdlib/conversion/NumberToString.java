package org.senthilvsh.saffron.stdlib.conversion;

import org.senthilvsh.saffron.ast.FunctionArgument;
import org.senthilvsh.saffron.common.Scope;
import org.senthilvsh.saffron.common.Type;
import org.senthilvsh.saffron.common.Variable;
import org.senthilvsh.saffron.runtime.NumberObj;
import org.senthilvsh.saffron.runtime.ReturnStatementResult;
import org.senthilvsh.saffron.runtime.StatementResult;
import org.senthilvsh.saffron.runtime.StringObj;
import org.senthilvsh.saffron.stdlib.NativeFunction;

import java.util.List;

public class NumberToString implements NativeFunction {
    @Override
    public String getName() {
        return "to_str";
    }

    @Override
    public List<FunctionArgument> getArguments() {
        return List.of(
                new FunctionArgument("source", Type.NUMBER)
        );
    }

    @Override
    public Type getReturnType() {
        return Type.STRING;
    }

    @Override
    public StatementResult run(Scope scope) {
        Variable sourceVar = scope.get("source");
        NumberObj sourceObj = (NumberObj) sourceVar.getValue();
        double source = sourceObj.getValue();

        String result;
        if ((long) source == source) {
            result = String.valueOf((long) source);
        } else {
            result = String.valueOf(source);
        }

        StringObj returnObj = new StringObj(result);

        return new ReturnStatementResult(returnObj);
    }
}
