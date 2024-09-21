package com.wibowo.fixtools.intellij;

public class FIXToolsException extends RuntimeException {

    public FIXToolsException(String message) {
        super(message);
    }

    public FIXToolsException(String message, Throwable cause) {
        super(message, cause);
    }
}
