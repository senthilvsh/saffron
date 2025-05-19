package org.senthilvsh.saffron.runtime;

public class UserException extends RuntimeException {
    private final String type;
    private final String message;

    public UserException(String type, String message) {
        this.type = type;
        this.message = message;
    }

    public String getType() {
        return type;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
