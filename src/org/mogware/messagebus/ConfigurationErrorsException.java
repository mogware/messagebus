package org.mogware.messagebus;

public class ConfigurationErrorsException extends RuntimeException {

    public ConfigurationErrorsException() {
    }

    public ConfigurationErrorsException(String message) {
        super(message);
    }

    public ConfigurationErrorsException(String message, Exception innerException) {
        super(message, innerException);
    }
}
