package org.senthilvsh.saffron.stdlib.console;

import org.senthilvsh.saffron.ast.FunctionArgument;
import org.senthilvsh.saffron.common.Scope;
import org.senthilvsh.saffron.common.Type;
import org.senthilvsh.saffron.runtime.BooleanObj;
import org.senthilvsh.saffron.runtime.ReturnStatementResult;
import org.senthilvsh.saffron.runtime.StatementResult;
import org.senthilvsh.saffron.stdlib.NativeFunction;

import java.util.List;

public class WriteBooleanNL implements NativeFunction {
    @Override
    public String getName() {
        return "writeln";
    }

    @Override
    public List<FunctionArgument> getArguments() {
        return List.of(new FunctionArgument("value", Type.BOOLEAN));
    }

    @Override
    public Type getReturnType() {
        return Type.VOID;
    }

    @Override
    public StatementResult run(Scope scope) {
        BooleanObj baseObj = (BooleanObj) scope.get("value").getValue();
        System.out.println(baseObj.getValue());
        return new ReturnStatementResult(null);
    }
}
