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
    public void run(Frame frame) throws FunctionReturn {
        Variable variable = frame.get("source");
        StringObj stringObj = (StringObj) variable.getValue();
        String source = stringObj.getValue();
        throw new FunctionReturn(new NumberObj(source.length()));
    }
}
