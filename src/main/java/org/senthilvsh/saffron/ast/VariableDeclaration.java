package org.senthilvsh.saffron.ast;

public class VariableDeclaration extends Statement {
    private final String name;
    private final String type;

    public VariableDeclaration(String name, String type, int position, int length) {
        super(position, length);
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}
