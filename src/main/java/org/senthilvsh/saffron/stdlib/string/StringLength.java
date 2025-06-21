package org.senthilvsh.saffron.stdlib.string;

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

public class StringLength implements NativeFunction {
    @Override
    public String getName() {
        return "str_length";
    }

    @Override
    public List<FunctionArgument> getArguments() {
        return List.of(new FunctionArgument("source", Type.STRING));
    }

    @Override
    public Type getReturnType() {
        return Type.NUMBER;
    }

    @Override
    public StatementResult run(Scope scope) {
        Variable variable = scope.get("source");
        StringObj stringObj = (StringObj) variable.getValue();
        String source = stringObj.getValue();

        int result = source.length();

        NumberObj returnObj = new NumberObj(result);

        return new ReturnStatementResult(returnObj);
    }
}
