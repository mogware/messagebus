package org.mogware.messagebus;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.mogware.system.Guid;

/**
* Represents an atomic unit of communication --a message or communiqué-- that
* can or has been transported over a communication medium, which holds both the
* messages and metadata about those messages.
*/

public class ChannelMessage {
    private final int Inactive = -1;
    private final Guid messageId;
    private final Guid correlationId;
    private final URI returnAddress;
    private final Map<String, String> headers;
    private final List<Object> messages;
    private final List<Object> immutable;

    private LocalDateTime dispatched;
    private Object activeMessage;
    private int activeIndex;
    private LocalDateTime expiration;
    private boolean persistent;

    public Guid getMessageId() {
        return this.messageId;
    }

    public Guid getCorrelationId() {
        return this.correlationId;
    }

    public URI getReturnAddress() {
        return this.returnAddress;
    }

    public Map<String, String> getHeaders() {
        return this.headers;
    }

    public List<Object> getMessages() {
        return this.immutable;
    }

    public Object getActiveMessage() {
        return this.activeMessage;
    }

    public void setActiveMessage(Object value) {
        this.activeMessage = value;
    }

    public int getActiveIndex() {
        return this.activeIndex;
    }

    public void setActiveIndex(int value) {
        this.activeIndex = value;
    }

    public LocalDateTime getExpiration() {
        return this.expiration;
    }

    public void setExpiration(LocalDateTime value) {
        this.expiration = value;
    }

    public boolean getPersistent() {
        return this.persistent;
    }

    public void setPersistent(boolean value) {
        this.persistent = value;
    }

    public LocalDateTime getDispatched() {
        return this.dispatched;
    }

    public void setDispatched(LocalDateTime value) {
        this.dispatched = value;
    }

    public boolean moveNext() {
        if (++this.activeIndex >= this.messages.size()) {
            this.reset();
            return false;
        }

        this.setActiveMessage(this.messages.get(this.getActiveIndex()));
        return true;
    }

    public void reset() {
        this.setActiveIndex(Inactive);
        this.setActiveMessage(null);
    }

    public ChannelMessage(Guid messageId, Guid correlationId, URI returnAddress,
            Map<String, String> headers, Iterable<Object> messages) {
        this.messageId = messageId;
        this.correlationId = correlationId;
        this.returnAddress = returnAddress;
        this.headers = headers != null ? headers : new HashMap<>();
        this.messages = messages == null ? new ArrayList<>() :
            StreamSupport.stream(messages.spliterator(), false)
                .filter((x) -> x != null)
                .collect(Collectors.toList());
        this.immutable = Collections.unmodifiableList(this.messages);
        this.activeIndex = Inactive;
        this.dispatched = LocalDateTime.MIN;
        this.expiration = LocalDateTime.MIN;
    }
}
