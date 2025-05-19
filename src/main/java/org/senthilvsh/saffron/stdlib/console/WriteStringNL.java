package org.senthilvsh.saffron.stdlib.console;

import org.senthilvsh.saffron.ast.FunctionArgument;
import org.senthilvsh.saffron.common.Frame;
import org.senthilvsh.saffron.common.Type;
import org.senthilvsh.saffron.runtime.ReturnStatementResult;
import org.senthilvsh.saffron.runtime.StatementResult;
import org.senthilvsh.saffron.runtime.StatementResultType;
import org.senthilvsh.saffron.runtime.StringObj;
import org.senthilvsh.saffron.stdlib.NativeFunction;

import java.util.List;

public class WriteStringNL implements NativeFunction {
    @Override
    public String getName() {
        return "writeln";
    }

    @Override
    public List<FunctionArgument> getArguments() {
        return List.of(new FunctionArgument("value", Type.STRING));
    }

    @Override
    public Type getReturnType() {
        return Type.VOID;
    }

    @Override
    public StatementResult run(Frame frame) {
        StringObj baseObj = (StringObj) frame.get("value").getValue();
        System.out.println(baseObj.getValue());
        return new ReturnStatementResult(null);
    }
}
