package org.senthilvsh.saffron.common;

import java.util.Stack;

public class Scopes {
    private final Stack<Scope> stack = new Stack<>();

    public int depth = 0;

    public void addFunctionScope() {
        Scope newScope = new Scope();
        if (!stack.isEmpty()) {
            Scope scope = stack.peek();
            for (Variable v : scope.values()) {
                newScope.put(v.getName(), v);
            }
        }
        push(newScope);
    }

    public void addBlockScope() {
        Scope newScope = new BlockScope();
        if (!stack.isEmpty()) {
            Scope scope = stack.peek();
            for (Variable v : scope.values()) {
                newScope.put(v.getName(), v);
            }
        }
        push(newScope);
    }

    private void push(Scope item) {
        if (!(item instanceof BlockScope)) {
            depth++;
        }
        stack.push(item);
    }

    public synchronized void remove() {
        Scope popped = stack.pop();
        if (!(popped instanceof BlockScope)) {
            depth--;
        }
    }

    public Scope current() {
        return stack.peek();
    }

    public int getDepth() {
        return depth;
    }
}
