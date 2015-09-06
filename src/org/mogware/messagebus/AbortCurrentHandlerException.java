package org.mogware.messagebus;

public class AbortCurrentHandlerException extends RuntimeException {

    public AbortCurrentHandlerException() {
    }

    public AbortCurrentHandlerException(String message) {
        super(message);
    }

    public AbortCurrentHandlerException(String message,
            Exception innerException) {
        super(message, innerException);
    }
}
