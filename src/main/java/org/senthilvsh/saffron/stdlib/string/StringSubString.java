package org.senthilvsh.saffron.stdlib.string;

import org.senthilvsh.saffron.ast.FunctionArgument;
import org.senthilvsh.saffron.common.Frame;
import org.senthilvsh.saffron.common.Type;
import org.senthilvsh.saffron.common.Variable;
import org.senthilvsh.saffron.runtime.FunctionReturn;
import org.senthilvsh.saffron.runtime.NumberObj;
import org.senthilvsh.saffron.runtime.StringObj;
import org.senthilvsh.saffron.stdlib.NativeFunction;

import java.util.List;

public class StringSubString implements NativeFunction {
    @Override
    public String getName() {
        return "str_substr";
    }

    @Override
    public List<FunctionArgument> getArguments() {
        return List.of(
                new FunctionArgument("source", Type.STRING),
                new FunctionArgument("start", Type.NUMBER),
                new FunctionArgument("end", Type.NUMBER)
        );
    }

    @Override
    public Type getReturnType() {
        return Type.STRING;
    }

    @Override
    public void run(Frame frame) throws FunctionReturn {
        Variable sourceVar = frame.get("source");
        StringObj sourceObj = (StringObj) sourceVar.getValue();
        String source = sourceObj.getValue();

        Variable startVar = frame.get("start");
        NumberObj startObj = (NumberObj) startVar.getValue();
        double start = startObj.getValue();

        Variable endVar = frame.get("end");
        NumberObj endValue = (NumberObj) endVar.getValue();
        double end = endValue.getValue();

        String result = source.substring((int) start, (int) end + 1);

        StringObj returnObj = new StringObj(result);

        throw new FunctionReturn(returnObj);
    }
}
