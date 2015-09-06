package org.mogware.messagebus.channels;

import org.mogware.messagebus.ChannelConnectionException;
import org.mogware.messagebus.ChannelEnvelope;
import org.mogware.messagebus.ChannelGroupConfiguration;
import org.mogware.messagebus.ChannelMessage;
import org.mogware.messagebus.ChannelTransaction;
import org.mogware.messagebus.DeliveryContext;
import org.mogware.messagebus.DependencyResolver;
import org.mogware.messagebus.DispatchContext;
import org.mogware.messagebus.MessagingChannel;
import org.mogware.system.ObjectDisposedException;
import org.mogware.system.delegates.Action1;

public class PooledDispatchChannel implements MessagingChannel {
    private final PooledDispatchConnector connector;
    private final MessagingChannel channel;
    private boolean disposed;
    private final int token;

    public PooledDispatchChannel(PooledDispatchConnector connector,
            MessagingChannel channel, int token) {
        if (connector == null)
            throw new NullPointerException("connector must not be null.");
        if (channel == null)
            throw new NullPointerException("channel must not be null.");
        if (token < 0)
            throw new IllegalArgumentException("token must not be negative.");
        this.connector = connector;
	this.channel = channel;
	this.token = token;
    }

    @Override
    public void beginShutdown() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void receive(Action1<DeliveryContext> callback) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void send(ChannelEnvelope envelope) {
        if (envelope == null)
            throw new NullPointerException("envelope must not be null.");
        if (this.disposed)
            throw new ObjectDisposedException(this.getClass().getName());
        this.trySend(envelope);
    }

    protected void trySend(ChannelEnvelope envelope) {
        try {
            this.channel.send(envelope);
        } catch (ChannelConnectionException ex) {
            this.connector.teardown(this.channel, this.token);
            throw ex;
        }
    }

    @Override
    public boolean getActive() {
        return this.channel.getActive();
    }

    @Override
    public ChannelMessage getCurrentMessage() {
        return null;
    }

    @Override
    public DependencyResolver getCurrentResolver() {
        return this.channel.getCurrentResolver();
    }

    @Override
    public ChannelTransaction getCurrentTransaction() {
        return this.channel.getCurrentTransaction();
    }

    @Override
    public ChannelGroupConfiguration getCurrentConfiguration() {
        return this.channel.getCurrentConfiguration();
    }

    @Override
    public DispatchContext prepareDispatch(Object message,
            MessagingChannel actual) {
        return this.channel.prepareDispatch(message,
                actual != null ? actual : this);
    }

    @Override
    public void dispose() {
        if (this.disposed)
            return;
        this.disposed = true;
        this.connector.release(this.channel, this.token);
    }
}
