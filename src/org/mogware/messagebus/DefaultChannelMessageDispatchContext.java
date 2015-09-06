package org.mogware.messagebus;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.mogware.system.Guid;

public class DefaultChannelMessageDispatchContext implements DispatchContext {
    private final List<URI> recipients = new LinkedList<>();
    private final MessagingChannel channel;
    private final ChannelMessage channelMessage;
    private boolean dispatched;

    public DefaultChannelMessageDispatchContext(MessagingChannel channel,
            ChannelMessage channelMessage) {
        if (channel == null)
            throw new NullPointerException("channel must not be null");
        if (channelMessage == null)
            throw new NullPointerException("channelMessage must not be null");
        this.channel = channel;
        this.channelMessage = channelMessage;
    }

    @Override
    public int getMessageCount() {
        return this.dispatched ? 0 : 1;
    }

    @Override
    public int getHeaderCount() {
        return 0;
    }

    @Override
    public DispatchContext withMessage(Object message) {
        throw new UnsupportedOperationException(
                "The message collection cannot be modified.");
    }

    @Override
    public DispatchContext withMessages(Object[] messages) {
        throw new UnsupportedOperationException(
                "The message collection cannot be modified.");
    }

    @Override
    public DispatchContext withCorrelationId(Guid correlationId) {
        throw new UnsupportedOperationException(
                "A correlation identifier is already set.");
    }

    @Override
    public DispatchContext withHeader(String key, String value) {
        throw new UnsupportedOperationException(
                "The headers cannot be modified.");
    }

    @Override
    public DispatchContext withHeaders(Map<String, String> headers) {
        throw new UnsupportedOperationException(
                "The headers cannot be modified.");
    }

    @Override
    public DispatchContext withRecipient(URI recipient) {
        if (recipient == null)
            throw new NullPointerException("recipient must not be null");
        this.recipients.add(recipient);
        return this;
    }

    @Override
    public DispatchContext withState(Object state) {
        throw new UnsupportedOperationException(
                "Envelope state cannot be specified.");
    }

    @Override
    public ChannelTransaction send(Object[] messages) {
        if (this.dispatched)
            throw new IllegalStateException(
                    "The set of messages has already been dispatched.");
        this.dispatched = true;
        this.channel.send(new ChannelEnvelope(
                this.channelMessage,
                this.recipients,
                this.channelMessage
        ));
        return this.channel.getCurrentTransaction();
    }

    @Override
    public ChannelTransaction publish(Object[] messages) {
        throw new UnsupportedOperationException("Only send can be invoked.");
    }

    @Override
    public ChannelTransaction reply(Object[] messages) {
        throw new UnsupportedOperationException("Only send can be invoked.");
    }
}
