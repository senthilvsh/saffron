package org.senthilvsh.saffron.stdlib.console;

import org.senthilvsh.saffron.ast.FunctionArgument;
import org.senthilvsh.saffron.common.Scope;
import org.senthilvsh.saffron.common.Type;
import org.senthilvsh.saffron.runtime.NumberObj;
import org.senthilvsh.saffron.runtime.ReturnStatementResult;
import org.senthilvsh.saffron.runtime.StatementResult;
import org.senthilvsh.saffron.stdlib.NativeFunction;

import java.util.List;

public class WriteNumber implements NativeFunction {
    @Override
    public String getName() {
        return "write";
    }

    @Override
    public List<FunctionArgument> getArguments() {
        return List.of(new FunctionArgument("value", Type.NUMBER));
    }

    @Override
    public Type getReturnType() {
        return Type.VOID;
    }

    @Override
    public StatementResult run(Scope scope) {
        NumberObj numberObj = (NumberObj) scope.get("value").getValue();
        double value = numberObj.getValue();
        String str;
        if (value == (long) value) {
            str = String.valueOf((long) value);
        } else {
            str = String.valueOf(value);
        }
        System.out.print(str);
        return new ReturnStatementResult(null);
    }
}
