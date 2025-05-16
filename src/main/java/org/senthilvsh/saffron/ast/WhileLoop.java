package org.senthilvsh.saffron.ast;

public class WhileLoop extends Statement {
    private final Expression condition;
    private final Statement body;

    public WhileLoop(Expression condition, Statement body, int position, int length) {
        super(position, length);
        this.condition = condition;
        this.body = body;
    }

    public Expression getCondition() {
        return condition;
    }

    public Statement getBody() {
        return body;
    }
}
