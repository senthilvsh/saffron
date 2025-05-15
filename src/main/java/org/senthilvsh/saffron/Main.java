package org.senthilvsh.saffron;

import org.senthilvsh.saffron.ast.Program;
import org.senthilvsh.saffron.parser.Parser;
import org.senthilvsh.saffron.parser.ParserException;
import org.senthilvsh.saffron.runtime.BaseObj;
import org.senthilvsh.saffron.runtime.Interpreter;
import org.senthilvsh.saffron.runtime.InterpreterException;
import org.senthilvsh.saffron.runtime.NumberObj;
import org.senthilvsh.saffron.typecheck.TypeChecker;
import org.senthilvsh.saffron.typecheck.TypeCheckerException;

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
            System.err.printf("Unable to read input file '%s'%n", input);
            e.printStackTrace(System.err);
            return;
        }

        Parser parser = new Parser(source);
        Program program;
        try {
            program = parser.program();
        } catch (ParserException e) {
            System.err.println("Error while parsing program");
            System.out.println(source.substring(e.getPosition(), e.getPosition() + e.getLength()));
            return;
        }

        TypeChecker typeChecker = new TypeChecker();
        try {
            typeChecker.getType(program.getExpression());
        } catch (TypeCheckerException e) {
            System.err.println(e.getMessage());
            System.err.println(getLine(source, e.getPosition()));
            System.err.println(squggly(e.getPosition(), e.getLength()));
            System.err.println();
            return;
        }

        Interpreter interpreter = new Interpreter();
        try {
            BaseObj result = interpreter.evaluate(program.getExpression());
            if (result instanceof NumberObj numberObj) {
                System.out.println(numberObj.getValue());
            }
        } catch (InterpreterException e) {
            System.err.println("Error while executing program");
            e.printStackTrace(System.err);
        }
    }

    private static String squggly(int position, int length) {
        String str = "";
        for (int i = 0; i < position; i++) {
            str += " ";
        }
        for (int i = position; i < position + length; i++) {
            str += "^";
        }
        return str;
    }

    private static String getLine(String source, int position) {
        List<String> lines = Arrays.stream(source.split("\n")).toList();
        int lineStart = 0;
        for (String line : lines) {
            int lineEnd = lineStart + line.length();
            if (position >= lineStart && position <= lineEnd) {
                return line;
            }
            lineStart = lineEnd + 1;
        }
        return "";
    }
}
