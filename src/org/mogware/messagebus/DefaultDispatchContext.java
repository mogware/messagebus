package org.mogware.messagebus;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.mogware.system.Guid;

public class DefaultDispatchContext implements DispatchContext {
    private final Map<String, String> messageHeaders = new HashMap();
    private final List<Object> logicalMessages = new LinkedList();
    private final List<URI> recipients = new LinkedList();
    private final MessagingChannel channel;
    private final DispatchTable dispatchTable;
    private final ChannelMessageBuilder builder;
    private final URI returnAddress;
    private Guid correlationIdentifier;
    private Object applicationState;
    private boolean dispatched;
    private int headerCount = 0;
    private int messageCount = 0;

    public DefaultDispatchContext(MessagingChannel channel) {
        this.channel = channel;
        this.dispatchTable = this.channel.getCurrentConfiguration()
                .getDispatchTable();
        ChannelGroupConfiguration config = channel.getCurrentConfiguration();
        this.builder = config.getMessageBuilder();
        this.returnAddress = config.getReturnAddress();
    }

    @Override
    public int getMessageCount() {
        return this.messageCount;
    }

    @Override
    public int getHeaderCount() {
        return this.headerCount;
    }

    @Override
    public DispatchContext withMessage(Object message) {
        if (message == null)
            throw new NullPointerException("message must not be null");
        ChannelMessage channelMessage = message instanceof ChannelMessage ?
                (ChannelMessage) message : (ChannelMessage) null;
        if (channelMessage != null)
            return new DefaultChannelMessageDispatchContext(
                    this.channel, channelMessage
            );
        this.logicalMessages.add(message);
        this.messageCount++;
        return this;
    }

    @Override
    public DispatchContext withMessages(Object[] messages) {
        if (messages == null)
            throw new NullPointerException("messages must not be null");
        if (messages.length == 0)
            throw new IllegalArgumentException(
                    "The set of messages provided cannot be empty.");
        return this.addMessages(messages);
    }

    private DispatchContext addMessages(Object[] messages) {
        for (Object message: messages)
            if (message != null)
                this.withMessage(message);
        return this;
    }

    @Override
    public DispatchContext withCorrelationId(Guid correlationId) {
        this.correlationIdentifier = correlationId;
        return this;
    }

    @Override
    public DispatchContext withHeader(String key, String value) {
        if (key == null)
            throw new NullPointerException("key must not be null");
        Boolean alreadyAdded = this.messageHeaders.containsKey(key);
        if (value == null && alreadyAdded) {
            this.messageHeaders.remove(key);
            this.headerCount--;
        }
        else if (value == null)
            return this;
        else {
            if (!alreadyAdded)
                this.headerCount++;
            this.messageHeaders.put(key, value);
        }
        return this;
    }

    @Override
    public DispatchContext withHeaders(Map<String, String> headers) {
        if (headers == null)
            throw new NullPointerException("headers must not be null");
        headers.entrySet().stream().forEach((item) ->
                this.withHeader(item.getKey(), item.getValue())
        );
        return this;
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
        if (state == null)
            throw new NullPointerException("state must not be null");
        this.applicationState = state;
        return this;
    }

    @Override
    public ChannelTransaction send(Object[] messages) {
        if (messages != null)
            this.addMessages(messages);
        return this.dispatch(this.buildRecipients());
    }

    @Override
    public ChannelTransaction publish(Object[] messages) {
        if (messages != null)
            this.addMessages(messages);
        return this.dispatch(this.buildRecipients());
    }

    @Override
    public ChannelTransaction reply(Object[] messages) {
        if (this.channel.getCurrentMessage() == null)
            throw new IllegalStateException(
                    "A reply can only be sent because of an incoming message.");
        if (messages != null)
            this.addMessages(messages);
        ChannelMessage message = this.channel.getCurrentMessage();
        URI incomingReturnAddress = message.getReturnAddress();
        if (this.correlationIdentifier == Guid.empty)
            this.correlationIdentifier = this.channel.getCurrentMessage()
                    .getCorrelationId();
        return this.dispatch(new ArrayList<URI>() {{
            add(incomingReturnAddress != null ?
                incomingReturnAddress : ChannelEnvelope.unroutableMessageAddress
            );
        }});
    }

    protected ChannelTransaction dispatch(List<URI> targets) {
        if (this.dispatched)
            throw new IllegalStateException(
                    "The set of messages has already been dispatched.");
        if (this.logicalMessages.isEmpty())
            throw new IllegalStateException(
                    "No messages have been provided to dispatch.");
        ChannelMessage message = this.builder.build(this.correlationIdentifier,
                this.returnAddress, this.messageHeaders, this.logicalMessages);
        ChannelEnvelope envelope = new ChannelEnvelope(message, targets,
                this.applicationState != null ?
                    this.applicationState : this.channel.getCurrentMessage()
        );
        this.dispatch(envelope);
        this.dispatched = true;
        this.messageCount = 0;
        this.headerCount = 0;
        return this.channel.getCurrentTransaction();
    }

    protected void dispatch(ChannelEnvelope envelope) {
        this.channel.send(envelope);
    }

    protected List<URI> buildRecipients() {
        if (this.logicalMessages.isEmpty())
            throw new IllegalStateException("No messages have been specified.");
        Class type = this.logicalMessages.get(0).getClass();
        List<URI> discovered = new ArrayList<>(this.dispatchTable.getUri(type));
        discovered.addAll(this.recipients);
        return discovered.isEmpty() ?
            new ArrayList<URI>() {{
                add(ChannelEnvelope.unroutableMessageAddress);
            }} : discovered;
    }
}
