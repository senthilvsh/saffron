package org.senthilvsh.saffron.common;

import java.util.Stack;

public class FrameStack extends Stack<Frame> {
    public void newFrame() {
        Frame frame = peek();
        Frame newFrame = new Frame();
        for (Variable v : frame.values()) {
            newFrame.put(v.getName(), new Variable(v.getName(), v.getType(), v.getValue(), false));
        }
        push(newFrame);
    }
}
