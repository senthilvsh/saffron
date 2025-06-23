package org.senthilvsh.saffron.stdlib.console;

import org.senthilvsh.saffron.runtime.ReturnStatementResult;
import org.senthilvsh.saffron.runtime.Scope;
import org.senthilvsh.saffron.runtime.StatementResult;
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
    public List<String> getArguments() {
        return new ArrayList<>();
    }

    @Override
    public StatementResult run(Scope scope) {
        return new ReturnStatementResult(new Scanner(System.in).nextLine());
    }
}
