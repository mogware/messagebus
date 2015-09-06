package org.mogware.messagebus;

import org.mogware.system.ObjectDisposedException;
import org.mogware.system.delegates.Action1;

public class SynchronousChannelGroup implements ChannelGroup {
    private final ChannelConnector connector;
    private final ChannelGroupConfiguration configuration;
    private boolean initialized;
    private boolean disposed;

    public SynchronousChannelGroup(ChannelConnector connector,
            ChannelGroupConfiguration configuration) {
        if (connector == null)
            throw new NullPointerException("connector must not be null");
        if (configuration == null)
            throw new NullPointerException("configuration must not be null");
        this.connector = connector;
        this.configuration = configuration;
    }

    @Override
    public boolean getDispatchOnly() {
        return this.configuration.getDispatchOnly();
    }

    @Override
    public void initialize() {
        if (this.disposed)
            throw new ObjectDisposedException(this.getClass().getName());
        this.initialized = true;
    }

    @Override
    public MessagingChannel openChannel() {
        if (this.disposed)
            throw new ObjectDisposedException(this.getClass().getName());
        if (!this.initialized)
            throw new IllegalStateException("Channel group not initialized.");
        return this.connector.connect(this.configuration.getGroupName());
    }

    @Override
    public void beginReceive(Action1<DeliveryContext> callback) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public boolean beginDispatch(Action1<DispatchContext> callback) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void dispose() {
        this.disposed = true;
    }
}
