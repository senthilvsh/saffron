package org.senthilvsh.saffron;

import org.senthilvsh.saffron.ast.Program;
import org.senthilvsh.saffron.common.SaffronException;
import org.senthilvsh.saffron.parser.Parser;
import org.senthilvsh.saffron.runtime.Interpreter;
import org.senthilvsh.saffron.validate.Validator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: saffron <input>");
            return;
        }

        String input = args[0];

        String source;
        try {
            source = Files.readString(Path.of(input));
        } catch (IOException e) {
            System.err.printf("Unable to read input file '%s'. Reason: %s%n", input, e.getMessage());
            return;
        }

        Parser parser = new Parser(source);
        Validator validator = new Validator();
        Interpreter interpreter = new Interpreter();
        try {
            Program program = parser.program();
            // TODO: Print all parsing errors from the ParseResult
            validator.validate(program);
            interpreter.execute(program);
        } catch (SaffronException e) {
            printError(e.getMessage(), source, e.getPosition(), e.getLength());
        }
    }

    private static void printError(String message, String source, int position, int length) {
        // TODO: Print line number
        System.err.println(message + "\n");
        LineInfo lineInfo = getLine(source, position);
        if (lineInfo != null) {
            System.err.println(lineInfo.line);
            System.err.println(squiggly(lineInfo.position, length));
        }
        System.err.println();
    }

    private static String squiggly(int position, int length) {
        return " ".repeat(Math.max(0, position)) + "^".repeat(Math.max(0, position + length - position));
    }

    private static LineInfo getLine(String source, int position) {
        List<String> lines = Arrays.stream(source.split("\n")).toList();
        int lineStart = 0;
        for (String line : lines) {
            int lineEnd = lineStart + line.length();
            if (position >= lineStart && position <= lineEnd) {
                return new LineInfo(line, position - lineStart, line.length());
            }
            lineStart = lineEnd + 1;
        }
        return null;
    }

    private record LineInfo(String line, int position, int length) {
    }
}
