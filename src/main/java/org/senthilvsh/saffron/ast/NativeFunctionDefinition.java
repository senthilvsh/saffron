package org.senthilvsh.saffron.ast;

import org.senthilvsh.saffron.common.Type;
import org.senthilvsh.saffron.stdlib.NativeFunction;

import java.util.List;
import java.util.stream.Collectors;

public class NativeFunctionDefinition extends FunctionDefinition {
    private final String name;
    private final List<FunctionArgument> arguments;
    private final Type returnType;
    private NativeFunction function;

    public NativeFunctionDefinition(String name, List<FunctionArgument> arguments, Type returnType, NativeFunction function) {
        super(name, arguments, null, returnType, 0, 0, 0, 0);
        this.name = name;
        this.arguments = arguments;
        this.returnType = returnType;
        this.function = function;
    }

    public String getName() {
        return name;
    }

    public List<FunctionArgument> getArguments() {
        return arguments;
    }

    public Type getReturnType() {
        return returnType;
    }

    public NativeFunction getFunction() {
        return function;
    }

    public String getSignature() {
        return name + "_" + arguments.stream().map(a -> a.getType().getName().toLowerCase()).collect(Collectors.joining("_"));
    }
}
