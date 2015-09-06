package org.mogware.messagebus;

import static org.mogware.messagebus.ExtensionMethods.tryDispose;
import org.mogware.system.ObjectDisposedException;
import org.mogware.system.delegates.Action0;
import org.mogware.system.delegates.Action1;

public class DefaultChannelGroup implements ChannelGroup {
    private final ChannelConnector connector;
    private final ChannelGroupConfiguration configuration;
    private final WorkerGroup<MessagingChannel> workers;
    private boolean receiving;
    private boolean initialized;
    private boolean disposed;

    public DefaultChannelGroup(ChannelConnector connector,
            ChannelGroupConfiguration configuration,
            WorkerGroup<MessagingChannel> workers) {
        this.connector = connector;
        this.configuration = configuration;
        this.workers = workers;
    }

    @Override
    public boolean getDispatchOnly() {
        return this.configuration.getDispatchOnly();
    }

    @Override
    public synchronized void initialize() {
        if (this.initialized)
            return;
        this.initialized = true;
        if (this.disposed)
            throw new ObjectDisposedException(this.getClass().getName());
        this.workers.initialize(()->this.tryConnect(), ()->this.canConnect());
        if (!this.getDispatchOnly())
            return;
        this.workers.startQueue();
        this.tryOperation(() -> this.connect().dispose());
    }

    @Override
    public MessagingChannel openChannel() {
        return this.connect();
    }

    @Override
    public synchronized void beginReceive(
            Action1<DeliveryContext> callback) {
        if (callback == null)
            throw new NullPointerException("callback must not be null.");
        if (this.disposed)
            throw new ObjectDisposedException(this.getClass().getName());
        if (!this.initialized)
            throw new IllegalStateException("Channel group not initialized.");
        if (this.receiving)
            throw new IllegalStateException("Callback already been provided.");
        if (this.configuration.getDispatchOnly())
            throw new IllegalStateException("Dispatch-only cannot receive.");
        this.receiving = true;
        this.workers.startActivity((worker) -> this.tryOperation(
            () -> worker.getState().receive(
                (context) -> worker.performOperation(()->callback.run(context))
            )
        ));
    }

    @Override
    public boolean beginDispatch(Action1<DispatchContext> callback) {
        if (callback == null)
            throw new NullPointerException("callback must not be null.");
        if (!this.initialized)
            throw new IllegalStateException("Channel group not initialized.");
        if (!this.configuration.getDispatchOnly())
            throw new IllegalStateException("It is not a dispatch-only group.");
        return this.workers.enqueue(
            (worker) -> this.tryBeginDispatch(worker, callback)
        );
    }

    protected boolean canConnect() {
        MessagingChannel channel = this.tryConnect();
        if (channel == null)
            return false;
        channel.dispose();
        return true;
    }

    protected MessagingChannel tryConnect() {
        try {
            return this.connect();
        } catch (ChannelConnectionException ex) {
            return null;
        }
    }

    protected void tryOperation(Action0 callback) {
        try {
            callback.run();
        } catch (ChannelConnectionException ex) {
            this.tryOperation(() -> this.workers.restart());
        }
    }

    protected MessagingChannel connect() {
        if (!this.initialized)
            throw new IllegalStateException("Channel group not initialized.");
        if (this.disposed)
            throw new ObjectDisposedException(this.getClass().getName());
        return this.connector.connect(this.configuration.getGroupName());
    }

    protected void tryBeginDispatch(WorkItem<MessagingChannel> worker,
            Action1<DispatchContext> callback) {
        this.tryOperation(() -> {
            try {
                callback.run(worker.getState().prepareDispatch(null, null));
            } catch (ChannelConnectionException ex) {
                this.beginDispatch(callback);
                throw ex;
            }
        });
    }

    @Override
    public synchronized void dispose() {
        if (this.disposed)
            return;
        this.disposed = true;
        tryDispose(workers, false);
    }
}
