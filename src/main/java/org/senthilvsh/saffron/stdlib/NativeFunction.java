package org.senthilvsh.saffron.stdlib;

import org.senthilvsh.saffron.runtime.NativeFunctionException;
import org.senthilvsh.saffron.runtime.Scope;
import org.senthilvsh.saffron.runtime.StatementResult;

import java.util.List;

public interface NativeFunction {
    String getName();

    List<String> getArguments();

    StatementResult run(Scope scope) throws NativeFunctionException;
}
