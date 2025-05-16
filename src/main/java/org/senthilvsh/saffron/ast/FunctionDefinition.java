package org.senthilvsh.saffron.ast;

import org.senthilvsh.saffron.common.Type;

import java.util.List;
import java.util.stream.Collectors;

public class FunctionDefinition extends Statement {
    private final String name;
    private final List<FunctionArgument> arguments;
    private final BlockStatement body;
    private final Type returnType;

    public FunctionDefinition(String name, List<FunctionArgument> arguments, BlockStatement body, Type returnType,
                              int position, int length) {
        super(position, length);
        this.name = name;
        this.arguments = arguments;
        this.body = body;
        this.returnType = returnType;
    }

    public String getName() {
        return name;
    }

    public List<FunctionArgument> getArguments() {
        return arguments;
    }

    public BlockStatement getBody() {
        return body;
    }

    public Type getReturnType() {
        return returnType;
    }

    public String getSignature() {
        return name + "_" + arguments.stream().map(a -> a.getType().getName().toLowerCase()).collect(Collectors.joining("_"));
    }
}
