package org.senthilvsh.saffron.ast;

public class VariableDeclaration extends Statement {
    private final String name;
    private final Expression expression;

    public VariableDeclaration(String name, Expression expression, int position, int length) {
        super(position, length);
        this.name = name;
        this.expression = expression;
    }

    public String getName() {
        return name;
    }

    public Expression getExpression() {
        return expression;
    }
}
