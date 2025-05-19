package org.senthilvsh.saffron.stdlib.string;

import org.senthilvsh.saffron.ast.FunctionArgument;
import org.senthilvsh.saffron.common.Frame;
import org.senthilvsh.saffron.common.Type;
import org.senthilvsh.saffron.common.Variable;
import org.senthilvsh.saffron.runtime.BooleanObj;
import org.senthilvsh.saffron.runtime.ReturnStatementResult;
import org.senthilvsh.saffron.runtime.StatementResult;
import org.senthilvsh.saffron.runtime.StringObj;
import org.senthilvsh.saffron.stdlib.NativeFunction;

import java.util.List;

public class StringEndsWith implements NativeFunction {
    @Override
    public String getName() {
        return "str_endswith";
    }

    @Override
    public List<FunctionArgument> getArguments() {
        return List.of(
                new FunctionArgument("source", Type.STRING),
                new FunctionArgument("search", Type.STRING)
        );
    }

    @Override
    public Type getReturnType() {
        return Type.BOOLEAN;
    }

    @Override
    public StatementResult run(Frame frame) {
        Variable sourceVar = frame.get("source");
        StringObj sourceObj = (StringObj) sourceVar.getValue();
        String source = sourceObj.getValue();

        Variable searchVar = frame.get("search");
        StringObj searchObj = (StringObj) searchVar.getValue();
        String search = searchObj.getValue();

        boolean result = source.endsWith(search);

        BooleanObj returnObj = new BooleanObj(result);

        return new ReturnStatementResult(returnObj);
    }
}
