package org.mogware.messagebus;

public class PoisonMessageException extends ChannelException {

    public PoisonMessageException() {
    }

    public PoisonMessageException(String message) {
        super(message);
    }

    public PoisonMessageException(String message,
            Exception innerException) {
        super(message, innerException);
    }
}
