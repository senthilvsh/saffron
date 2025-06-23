package org.senthilvsh.saffron;

import org.senthilvsh.saffron.ast.Program;
import org.senthilvsh.saffron.runtime.SaffronException;
import org.senthilvsh.saffron.parser.Parser;
import org.senthilvsh.saffron.runtime.Interpreter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class Main {
    public static void main(String[] args) {
        if (args.length < 1 || args[0].trim().isEmpty()) {
            System.out.println("Usage: saffron <input>");
            return;
        }

        Properties properties = new Properties();
        try {
            properties.load(Main.class.getClassLoader().getResourceAsStream("saffron.properties"));
        } catch (IOException e) {
            System.err.println("Unable to load application properties. Exiting now.");
            e.printStackTrace(System.err);
        }
        String version = properties.getProperty("saffron.version");

        if (args[0].trim().equals("--version") || args[0].trim().equals("--help")) {
            System.out.println("Saffron" + (version != null ? " v" + version : ""));
            System.out.println();
            System.out.println("Saffron is a simple, type-safe, general-purpose programming language.");
            return;
        }

        String input = args[0];

        String source;
        try {
            source = Files.readString(Path.of(input));
        } catch (IOException e) {
            System.err.printf("Unable to read input file '%s'%n", input);
            return;
        }

        try {
            Program program = new Parser(source).parse();
            new Interpreter().execute(program);
        } catch (SaffronException e) {
            printError(e, source);
        }
    }

    private static void printError(SaffronException e, String source) {
        int position = e.getPosition();
        int length = e.getLength();
        String message = "Runtime Error: " + e.getMessage();
        System.err.println(message + "\n");
        LineInfo lineInfo = getLine(source, position);
        if (lineInfo != null) {
            String lineNoStr = "[Line:" + lineInfo.lineNo + "]    ";
            System.err.println(lineNoStr + lineInfo.line);
            System.err.println(" ".repeat(lineNoStr.length()) + squiggly(lineInfo.position, length));
        }
        System.err.println();
    }

    private static String squiggly(int position, int length) {
        return " ".repeat(Math.max(0, position)) + "^".repeat(Math.max(0, position + length - position));
    }

    private static LineInfo getLine(String source, int position) {
        List<String> lines = Arrays.stream(source.split("\n")).toList();
        int lineStart = 0;
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            int lineEnd = lineStart + line.length();
            if (position >= lineStart && position <= lineEnd) {
                return new LineInfo(line, i + 1, position - lineStart, line.length());
            }
            lineStart = lineEnd + 1;
        }
        return null;
    }

    private record LineInfo(String line, int lineNo, int position, int length) {
    }
}
