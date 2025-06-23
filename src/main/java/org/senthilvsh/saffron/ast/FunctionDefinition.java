package org.senthilvsh.saffron.ast;

import java.util.List;

public class FunctionDefinition extends Statement {
    private final String name;
    private final List<String> arguments;
    private final BlockStatement body;
    private final int namePosition;
    private final int nameLength;

    public FunctionDefinition(String name, List<String> arguments, BlockStatement body,
                              int position, int length, int namePosition, int nameLength) {
        super(position, length);
        this.name = name;
        this.arguments = arguments;
        this.body = body;
        this.namePosition = namePosition;
        this.nameLength = nameLength;
    }

    public String getName() {
        return name;
    }

    public List<String> getArguments() {
        return arguments;
    }

    public BlockStatement getBody() {
        return body;
    }

    public int getNamePosition() {
        return namePosition;
    }

    public int getNameLength() {
        return nameLength;
    }
}
