package org.senthilvsh.saffron;

import org.senthilvsh.saffron.ast.Program;
import org.senthilvsh.saffron.common.SaffronException;
import org.senthilvsh.saffron.parser.ParseError;
import org.senthilvsh.saffron.parser.ParseResult;
import org.senthilvsh.saffron.parser.Parser;
import org.senthilvsh.saffron.runtime.Interpreter;
import org.senthilvsh.saffron.runtime.RuntimeError;
import org.senthilvsh.saffron.validate.ValidationError;
import org.senthilvsh.saffron.validate.Validator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class Main {
    // TODO: Add user guide to readme
    // TODO: Add sample programs folder
    // TODO: Add support for single-line comments
    // TODO: Add support for multi-line comments
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: saffron <input>");
            return;
        }

        // TODO: Print version info

        String input = args[0];

        String source;
        try {
            source = Files.readString(Path.of(input));
        } catch (IOException e) {
            System.err.printf("Unable to read input file '%s'%n", input);
            return;
        }

        Parser parser = new Parser(source);
        Validator validator = new Validator();
        Interpreter interpreter = new Interpreter();
        try {
            ParseResult result = parser.parse();
            for (ParseError e : result.getErrors()) {
                printError(e, source);
            }
            Program program = result.getProgram();
            // TODO: Do not validate if there are parse errors
            validator.validate(program);
            // TODO: Do not run program if there are parse or validation errors
            interpreter.execute(program);
        } catch (SaffronException e) {
            printError(e, source);
        }
    }

    private static void printError(SaffronException e, String source) {
        int position = e.getPosition();
        int length = e.getLength();
        String message = e.getMessage();
        if (e instanceof ValidationError) {
            message = "Validation Error: " + message;
        } else if (e instanceof RuntimeError) {
            message = "Runtime Error: " + message;
        }
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
