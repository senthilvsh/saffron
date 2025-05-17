package org.senthilvsh.saffron.ast;

public class VariableDeclaration extends Statement {
    private final String name;
    private final String type;
    private final Expression expression;

    public VariableDeclaration(String name, String type, Expression expression, int position, int length) {
        super(position, length);
        this.name = name;
        this.type = type;
        this.expression = expression;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public Expression getExpression() {
        return expression;
    }
}
