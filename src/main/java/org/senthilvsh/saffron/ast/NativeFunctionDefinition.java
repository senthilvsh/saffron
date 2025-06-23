package org.senthilvsh.saffron.ast;

import org.senthilvsh.saffron.stdlib.NativeFunction;

import java.util.List;

public class NativeFunctionDefinition extends FunctionDefinition {
    private final String name;
    private final List<String> arguments;
    private final NativeFunction function;

    public NativeFunctionDefinition(String name, List<String> arguments, NativeFunction function) {
        super(name, arguments, null, 0, 0, 0, 0);
        this.name = name;
        this.arguments = arguments;
        this.function = function;
    }

    public String getName() {
        return name;
    }

    public List<String> getArguments() {
        return arguments;
    }

    public NativeFunction getFunction() {
        return function;
    }
}
