package org.senthilvsh.saffron.ast;

public class ConditionalStatement extends Statement {
    private final Expression condition;
    private final Statement trueClause;
    private final Statement falseClause;

    public ConditionalStatement(Expression condition, Statement trueClause, Statement falseClause,
                                int position, int length) {
        super(position, length);
        this.condition = condition;
        this.trueClause = trueClause;
        this.falseClause = falseClause;
    }

    public Expression getCondition() {
        return condition;
    }

    public Statement getTrueClause() {
        return trueClause;
    }

    public Statement getFalseClause() {
        return falseClause;
    }
}
