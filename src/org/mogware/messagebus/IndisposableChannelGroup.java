package org.mogware.messagebus;

import org.mogware.system.delegates.Action1;

public class IndisposableChannelGroup implements ChannelGroup {
    private final ChannelGroup inner;

    public IndisposableChannelGroup(ChannelGroup inner) {
        if (inner == null)
            throw new NullPointerException("inner must not be null");
        this.inner = inner;
    }

    public ChannelGroup getInner() {
        return this.inner;
    }

    @Override
    public boolean getDispatchOnly() {
        return this.inner.getDispatchOnly();
    }

    @Override
    public void initialize() {
        this.inner.initialize();
    }

    @Override
    public MessagingChannel openChannel() {
        return this.inner.openChannel();
    }

    @Override
    public void beginReceive(Action1<DeliveryContext> callback) {
        this.inner.beginReceive(callback);
    }

    @Override
    public boolean beginDispatch(Action1<DispatchContext> callback) {
        return this.inner.beginDispatch(callback);
    }

    @Override
    public void dispose() {
    }
}
