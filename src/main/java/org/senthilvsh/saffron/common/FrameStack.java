package org.senthilvsh.saffron.common;

import java.util.Stack;

public class FrameStack extends Stack<Frame> {
    public int depth = 0;

    public void newFrame() {
        Frame newFrame = new Frame();
        if (!isEmpty()) {
            Frame frame = peek();
            for (Variable v : frame.values()) {
                newFrame.put(v.getName(), v);
            }
        }
        push(newFrame);
    }

    public void newBlockScope() {
        Frame newFrame = new BlockFrame();
        if (!isEmpty()) {
            Frame frame = peek();
            for (Variable v : frame.values()) {
                newFrame.put(v.getName(), v);
            }
        }
        push(newFrame);
    }

    @Override
    public Frame push(Frame item) {
        if (!(item instanceof BlockFrame)) {
            depth++;
        }
        return super.push(item);
    }

    @Override
    public synchronized Frame pop() {
        Frame popped = super.pop();
        if (!(popped instanceof BlockFrame)) {
            depth--;
        }
        return popped;
    }

    public int getDepth() {
        return depth;
    }
}
