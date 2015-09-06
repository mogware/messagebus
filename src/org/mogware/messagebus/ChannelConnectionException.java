package org.mogware.messagebus;

public class ChannelConnectionException extends ChannelException {

    public ChannelConnectionException() {
    }

    public ChannelConnectionException(String message) {
        super(message);
    }

    public ChannelConnectionException(String message,
            Exception innerException) {
        super(message, innerException);
    }
}
