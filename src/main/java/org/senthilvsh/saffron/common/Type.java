package org.senthilvsh.saffron.common;

import java.util.Objects;

public class Type {
    public static final Type NUMBER = new Type("number");
    public static final Type STRING = new Type("string");
    public static final Type BOOLEAN = new Type("boolean");

    private final String name;

    public Type(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Type type = (Type) o;
        return Objects.equals(name, type.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

    @Override
    public String toString() {
        return String.format("Type { name: %s }", name);
    }

    public static Type of(String name) {
        return switch (name) {
            case "num" -> NUMBER;
            case "str" -> STRING;
            case "bool" -> BOOLEAN;
            default -> throw new RuntimeException("Unexpected value: " + name);
        };
    }
}
