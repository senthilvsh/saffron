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
            e.printStackTrace(System.err);
            return;
        }

        TypeChecker typeChecker = new TypeChecker();
        try {
            typeChecker.getType(program.getExpression());
        } catch (TypeCheckerException e) {
            System.err.println("Program contains errors");
            e.printStackTrace(System.err);
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
}
