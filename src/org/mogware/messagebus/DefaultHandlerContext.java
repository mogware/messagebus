package org.mogware.messagebus;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.mogware.system.ObjectDisposedException;

public class DefaultHandlerContext implements HandlerContext {
    private final DeliveryContext delivery;
    private boolean continueHandling;
    private boolean deferred;
    private boolean disposed;

    public DefaultHandlerContext(DeliveryContext delivery) {
        if (delivery == null)
            throw new NullPointerException("delivery must not be null");
        this.delivery = delivery;
        this.continueHandling = true;
    }

    @Override
    public boolean getContinueHandling() {
        return this.continueHandling && this.getActive();
    }

    @Override
    public void dropMessage() {
        if (this.disposed)
            throw new ObjectDisposedException(this.getClass().getName());
        this.continueHandling = false;
    }

    @Override
    public void deferMessage() {
        if (this.deferred)
            return;
        this.deferred = true;
        this.dropMessage();
	this.delivery.prepareDispatch(null, null)
            .withMessage(this.delivery.getCurrentMessage())
            .withRecipient(ChannelEnvelope.loopbackAddress)
            .send(null);
    }

    @Override
    public void forwardMessage(Iterable<URI> recipients) {
        if (this.disposed)
            throw new ObjectDisposedException(this.getClass().getName());
        if (recipients == null)
            throw new NullPointerException("recipients must not be null");
        List<URI> parsed = StreamSupport.stream(recipients.spliterator(), false)
                .filter((x) -> x != null)
                .collect(Collectors.toList());
        if (parsed.isEmpty())
            throw new IllegalStateException("No recipients specified.");
        DispatchContext dispatch = this.delivery.prepareDispatch(
                this.delivery.getCurrentMessage(), null
        );
        for (URI recipient: parsed)
            dispatch = dispatch.withRecipient(recipient);
        dispatch.send(null);
    }

    @Override
    public boolean getActive() {
        return this.delivery.getActive();
    }

    @Override
    public ChannelMessage getCurrentMessage() {
        return this.delivery.getCurrentMessage();
    }

    @Override
    public DependencyResolver getCurrentResolver() {
        return this.delivery.getCurrentResolver();
    }

    @Override
    public ChannelTransaction getCurrentTransaction() {
        return this.delivery.getCurrentTransaction();
    }

    @Override
    public ChannelGroupConfiguration getCurrentConfiguration() {
        return this.delivery.getCurrentConfiguration();
    }

    @Override
    public DispatchContext prepareDispatch(Object message,
            MessagingChannel channel) {
        if (this.disposed)
            throw new ObjectDisposedException(this.getClass().getName());
        return this.delivery.prepareDispatch(message, channel);
    }

    @Override
    public void dispose() {
        this.disposed = true;
        this.continueHandling = false;
    }
}
