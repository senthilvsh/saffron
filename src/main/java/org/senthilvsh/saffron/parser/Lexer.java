package org.senthilvsh.saffron.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {
    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("(\\s+)([\\s\\S]*)");
    private static final Pattern NUMBER_PATTERN = Pattern.compile("((([0-9]+)([.]?)([0-9]+))|([0-9]+))([\\s\\S]*)");
    private static final Pattern STRING_PATTERN = Pattern.compile("((\")([^\"]*)(\"))([\\s\\S]*)");
    private static final Pattern BOOLEAN_PATTERN = Pattern.compile("(true|false)([\\s\\S]*)");
    private static final Pattern OPERATOR_PATTERN = Pattern.compile("(&&|\\|\\||>=|<=|>|<|==|!=|=|\\+|-|\\*|/|%)([\\s\\S]*)");
    private static final Pattern SYMBOL_PATTERN = Pattern.compile("([{}();:])([\\s\\S]*)");
    private static final Pattern IDENTIFIER_PATTERN = Pattern.compile("(([_a-zA-Z]+)([_a-zA-Z0-9]*))([\\s\\S]*)");
    private static final Pattern SINGLE_LINE_COMMENT_PATTERN = Pattern.compile("((//)([^\\n]*)(\\n))([\\s\\S]*)");
    private static final Pattern MULTI_LINE_COMMENT_START_PATTERN = Pattern.compile("(/\\*)([\\s\\S]*)");
    private static final Pattern MULTI_LINE_COMMENT_END_PATTERN = Pattern.compile("(\\*/)([\\s\\S]*)");

    private final String source;

    private int position;

    public Lexer(String source) {
        this.source = source;
        this.position = 0;
    }

    public Token next() {
        if (position >= source.length()) {
            return null;
        }

        Matcher m;

        m = WHITESPACE_PATTERN.matcher(source.substring(position));
        if (m.matches()) {
            String match = m.group(1);
            position += match.length();
            return next();
        }

        m = SINGLE_LINE_COMMENT_PATTERN.matcher(source.substring(position));
        if (m.matches()) {
            String match = m.group(1);
            position += match.length();
            return next();
        }

        m = MULTI_LINE_COMMENT_START_PATTERN.matcher(source.substring(position));
        if (m.matches()) {
            int startPosition = position;
            String comment = m.group(1);
            position += comment.length();
            m = MULTI_LINE_COMMENT_END_PATTERN.matcher(source.substring(position));
            while (!m.matches()) {
                comment += source.charAt(position);
                position++;
                if (position >= source.length()) {
                    break;
                }
                m = MULTI_LINE_COMMENT_END_PATTERN.matcher(source.substring(position));
            }
            if (m.matches()) {
                comment += m.group(1);
                position += m.group(1).length();
            }
            return new Token(TokenType.COMMENT, comment, startPosition);
        }

        m = NUMBER_PATTERN.matcher(source.substring(position));
        if (m.matches()) {
            String match = m.group(1);
            Token token = new Token(TokenType.NUMBER, match, position);
            position += match.length();
            return token;
        }

        m = STRING_PATTERN.matcher(source.substring(position));
        if (m.matches()) {
            String match = m.group(1);
            Token token = new Token(TokenType.STRING, match, position);
            position += match.length();
            return token;
        }

        m = BOOLEAN_PATTERN.matcher(source.substring(position));
        if (m.matches()) {
            String match = m.group(1);
            Token token = new Token(TokenType.BOOLEAN, match, position);
            position += match.length();
            return token;
        }

        m = OPERATOR_PATTERN.matcher(source.substring(position));
        if (m.matches()) {
            String match = m.group(1);
            Token token = new Token(TokenType.OPERATOR, match, position);
            position += match.length();
            return token;
        }

        m = SYMBOL_PATTERN.matcher(source.substring(position));
        if (m.matches()) {
            String match = m.group(1);
            Token token = new Token(TokenType.SYMBOL, match, position);
            position += match.length();
            return token;
        }

        m = IDENTIFIER_PATTERN.matcher(source.substring(position));
        if (m.matches()) {
            String match = m.group(1);
            Token token;
            if (isKeyword(match)) {
                token = new Token(TokenType.KEYWORD, match, position);
            } else {
                token = new Token(TokenType.IDENTIFIER, match, position);
            }
            position += match.length();
            return token;
        }

        String value = String.valueOf(source.charAt(position));
        Token token = new Token(TokenType.INVALID, value, position);
        position++;
        return token;
    }

    private boolean isKeyword(String identifier) {
        return "var".equals(identifier) ||
                "num".equals(identifier) ||
                "str".equals(identifier) ||
                "bool".equals(identifier) ||
                "print".equals(identifier) ||
                "if".equals(identifier) ||
                "else".equals(identifier);
    }
}
