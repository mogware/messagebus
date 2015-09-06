package org.mogware.messagebus;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
* Represents a message with a collection of recipients to be dispatched.
*/

public class ChannelEnvelope {
    public static URI loopbackAddress = URI.create("default://loopback/");
    public static URI deadLetterAddress =
            URI.create("default://dead-letter-queue/");
    public static URI unhandledMessageAddress =
            URI.create("default://unhandled-message-queue/");
    public static URI unroutableMessageAddress =
            URI.create("default://unroutable-message-queue/");

    private ChannelMessage message;
    private List<URI> recipients;
    private Object state;

    public ChannelMessage getMessage() {
        return this.message;
    }

    public void setMessage(ChannelMessage value) {
        this.message = value;
    }

    public List<URI> getRecipients() {
        return this.recipients;
    }

    public void setRecipients(List<URI> value) {
        this.recipients = value;
    }

    public Object getState() {
        return this.state;
    }

    public void setState(Object value) {
        this.state = value;
    }

    public ChannelEnvelope(ChannelMessage message, Iterable<URI> recipients,
            Object state) {
        if (message == null)
            throw new NullPointerException("message must not be null."); 
        if (recipients == null)
            throw new NullPointerException("recipients must not be null.");         
        this.message = message;
        this.recipients = Collections.unmodifiableList(
            StreamSupport.stream(recipients.spliterator(), false)
                .filter((x) -> x != null)
                .collect(Collectors.toList()));
        if (this.recipients.isEmpty())
            throw new IllegalArgumentException("No recipients were provided.");
        this.state = state;
    }
}
