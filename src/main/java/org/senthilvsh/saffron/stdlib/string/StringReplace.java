package org.senthilvsh.saffron.stdlib.string;

import org.senthilvsh.saffron.ast.FunctionArgument;
import org.senthilvsh.saffron.common.Frame;
import org.senthilvsh.saffron.common.Type;
import org.senthilvsh.saffron.common.Variable;
import org.senthilvsh.saffron.runtime.BooleanObj;
import org.senthilvsh.saffron.runtime.FunctionReturn;
import org.senthilvsh.saffron.runtime.StringObj;
import org.senthilvsh.saffron.stdlib.NativeFunction;

import java.util.List;

public class StringReplace implements NativeFunction {
    @Override
    public String getName() {
        return "str_replace";
    }

    @Override
    public List<FunctionArgument> getArguments() {
        return List.of(
                new FunctionArgument("source", Type.STRING),
                new FunctionArgument("search", Type.STRING),
                new FunctionArgument("replace", Type.STRING)
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

        Variable searchVar = frame.get("search");
        StringObj searchObj = (StringObj) searchVar.getValue();
        String search = searchObj.getValue();

        Variable replaceVar = frame.get("replace");
        StringObj replaceObj = (StringObj) replaceVar.getValue();
        String replace = replaceObj.getValue();

        String result = source.replace(search, replace);

        StringObj returnObj = new StringObj(result);

        throw new FunctionReturn(returnObj);
    }
}
