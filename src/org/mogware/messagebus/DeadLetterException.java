package org.mogware.messagebus;

import java.time.LocalDateTime;

public class DeadLetterException extends ChannelException {
    private final LocalDateTime expiration;

    public LocalDateTime getExpiration() {
        return this.expiration;
    }

    public DeadLetterException(LocalDateTime expiration) {
        this.expiration = expiration;
    }
}
