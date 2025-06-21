package org.senthilvsh.saffron.stdlib.console;

import org.senthilvsh.saffron.ast.FunctionArgument;
import org.senthilvsh.saffron.common.Scope;
import org.senthilvsh.saffron.common.Type;
import org.senthilvsh.saffron.runtime.ReturnStatementResult;
import org.senthilvsh.saffron.runtime.StatementResult;
import org.senthilvsh.saffron.runtime.StringObj;
import org.senthilvsh.saffron.stdlib.NativeFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ReadLine implements NativeFunction {
    @Override
    public String getName() {
        return "readln";
    }

    @Override
    public List<FunctionArgument> getArguments() {
        return new ArrayList<>();
    }

    @Override
    public Type getReturnType() {
        return Type.STRING;
    }

    @Override
    public StatementResult run(Scope scope) {
        return new ReturnStatementResult(new StringObj(new Scanner(System.in).nextLine()));
    }
}
