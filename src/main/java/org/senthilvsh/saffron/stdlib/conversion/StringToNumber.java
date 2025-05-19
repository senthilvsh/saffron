package org.senthilvsh.saffron.stdlib.conversion;

import org.senthilvsh.saffron.ast.FunctionArgument;
import org.senthilvsh.saffron.common.Frame;
import org.senthilvsh.saffron.common.Type;
import org.senthilvsh.saffron.common.Variable;
import org.senthilvsh.saffron.runtime.*;
import org.senthilvsh.saffron.stdlib.NativeFunction;

import java.util.List;

public class StringToNumber implements NativeFunction {
    @Override
    public String getName() {
        return "to_num";
    }

    @Override
    public List<FunctionArgument> getArguments() {
        return List.of(
                new FunctionArgument("source", Type.STRING)
        );
    }

    @Override
    public Type getReturnType() {
        return Type.NUMBER;
    }

    @Override
    public StatementResult run(Frame frame) throws NativeFunctionException {
        Variable sourceVar = frame.get("source");
        StringObj sourceObj = (StringObj) sourceVar.getValue();
        String source = sourceObj.getValue();

        double result;
        try {
            result = Double.parseDouble(source);
        } catch (NumberFormatException e) {
            throw new NativeFunctionException("FORMAT_EXCEPTION", "Not a valid number");
        }

        NumberObj returnObj = new NumberObj(result);

        return new ReturnStatementResult(returnObj);
    }
}
