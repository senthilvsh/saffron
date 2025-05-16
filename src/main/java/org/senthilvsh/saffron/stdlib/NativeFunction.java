package org.senthilvsh.saffron.stdlib;

import org.senthilvsh.saffron.ast.FunctionArgument;
import org.senthilvsh.saffron.common.Frame;
import org.senthilvsh.saffron.common.Type;
import org.senthilvsh.saffron.runtime.FunctionReturn;

import java.util.List;

public interface NativeFunction {
    String getName();

    List<FunctionArgument> getArguments();

    Type getReturnType();

    void run(Frame frame) throws FunctionReturn;
}
