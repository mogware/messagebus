package org.mogware.messagebus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import static org.mogware.messagebus.ExtensionMethods.tryDispose;
import org.mogware.system.KeyNotFoundException;
import org.mogware.system.ObjectDisposedException;
import org.mogware.system.delegates.Action1;

public class DefaultMessagingHost implements MessagingHost {
    private final Map<String, ChannelGroup> groups = new HashMap<>();
    private final List<ChannelConnector> connectors;
    private final ChannelGroupFactory factory;
    private boolean receiving;
    private boolean initialized;
    private boolean disposed;

    public DefaultMessagingHost(Iterable<ChannelConnector> connectors,
            ChannelGroupFactory factory) {
        if (connectors == null)
            throw new NullPointerException("connectors must not be null");
        if (factory == null)
            throw new NullPointerException("factory must not be null");
        this.connectors = StreamSupport.stream(connectors.spliterator(), false)
                .filter((x) -> x != null)
                .collect(Collectors.toList());
        if (this.connectors.isEmpty())
            throw new IllegalStateException("No connectors provided.");
        this.factory = factory;
    }

    @Override
    public ChannelGroup initialize() {
        synchronized (this) {
            if (this.disposed)
                throw new ObjectDisposedException(this.getClass().getName());
            if (!this.initialized)
                this.initializeChannelGroups();
            this.initialized = true;
        }
        return new IndisposableChannelGroup(
                this.groups.values().stream().findFirst().orElse(null)
        );
    }

    @Override
    public void beginReceive(Action1<DeliveryContext> callback) {
        if (callback == null)
            throw new NullPointerException("callback must not be null");
        synchronized (this) {
            if (this.disposed)
                throw new ObjectDisposedException(this.getClass().getName());
            if (!this.initialized)
                throw new IllegalStateException("Host not initialized.");
            if (this.receiving)
                throw new IllegalStateException(
                        "A callback has already been provided.");
            this.receiving = true;
            this.groups.values().stream()
                    .filter((x) -> !x.getDispatchOnly())
                    .forEach((x) -> x.beginReceive(callback));
        }
    }

    @Override
    public ChannelGroup getChannelGroup(String channelGroup) {
        if (channelGroup == null)
            throw new NullPointerException("channelGroup must not be null");
        synchronized (this) {
            if (this.disposed)
                throw new ObjectDisposedException(this.getClass().getName());
            if (!this.initialized)
                throw new IllegalStateException("Host not initialized.");
            if (this.groups.containsKey(channelGroup))
                return new IndisposableChannelGroup(
                        this.groups.get(channelGroup));
            throw new KeyNotFoundException(
                "Could not find a channel group from the key provided.");
        }
    }

    protected void initializeChannelGroups() {
        this.connectors.stream().forEach((ctor) -> {
            StreamSupport.stream(ctor.getChannelGroups().spliterator(), false)
                .forEach((config) -> this.addChannelGroup(config.getGroupName(),
                        this.factory.invoke(ctor, config)
                ));
        });
        if (this.groups.isEmpty())
            throw new ConfigurationErrorsException(
                    "No channel groups have been configured.");
    }

    protected void addChannelGroup(String name, ChannelGroup group) {
        group.initialize();
        this.groups.put(name, group);
    }

    @Override
    public void dispose() {
        synchronized (this) {
            if (this.disposed)
                return;
            this.disposed = true;
            this.groups.values().stream().forEach((group) ->
                    tryDispose(group, false));
            this.connectors.stream().forEach((connector) ->
                    tryDispose(connector, false));
        }
    }
}
