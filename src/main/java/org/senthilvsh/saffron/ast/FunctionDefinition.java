package org.senthilvsh.saffron.ast;

import org.senthilvsh.saffron.common.Type;

import java.util.List;
import java.util.stream.Collectors;

public class FunctionDefinition extends Statement {
    private final String name;
    private final List<FunctionArgument> arguments;
    private final BlockStatement body;
    private final Type returnType;
    private final int namePosition;
    private final int nameLength;

    public FunctionDefinition(String name, List<FunctionArgument> arguments, BlockStatement body, Type returnType,
                              int position, int length, int namePosition, int nameLength) {
        super(position, length);
        this.name = name;
        this.arguments = arguments;
        this.body = body;
        this.returnType = returnType;
        this.namePosition = namePosition;
        this.nameLength = nameLength;
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

    public int getNamePosition() {
        return namePosition;
    }

    public int getNameLength() {
        return nameLength;
    }

    public String getSignature() {
        return name + "_" + arguments.stream().map(a -> a.getType().getName().toLowerCase()).collect(Collectors.joining("_"));
    }
}
