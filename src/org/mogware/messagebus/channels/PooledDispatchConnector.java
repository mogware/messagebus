package org.mogware.messagebus.channels;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.StreamSupport;
import org.mogware.messagebus.ChannelConnector;
import org.mogware.messagebus.ChannelGroupConfiguration;
import org.mogware.messagebus.ConnectionState;
import static org.mogware.messagebus.ExtensionMethods.tryDispose;
import org.mogware.messagebus.MessagingChannel;
import org.mogware.system.ObjectDisposedException;

public class PooledDispatchConnector implements ChannelConnector {
    private final Map<MessagingChannel, Boolean> open =
            Collections.synchronizedMap(new HashMap<>());
    private final Map<String, List<MessagingChannel>> available =
            new HashMap<>();
    private final ChannelConnector connector;
    private AtomicInteger currentToken;
    private final static int disposed = Integer.MIN_VALUE;

    public PooledDispatchConnector(ChannelConnector connector) {
        if (connector == null)
            throw new NullPointerException("connector must not be null");
        StreamSupport.stream(connector.getChannelGroups().spliterator(), false)
                .filter((x) -> x.getDispatchOnly() && x.getSynchronous())
                .forEach((x) -> this.available.put(x.getGroupName(),
                        Collections.synchronizedList(new LinkedList<>())));
        this.connector = connector;
        this.currentToken.set(0);
    }

    @Override
    public ConnectionState getCurrentState() {
        return this.connector.getCurrentState();
    }

    @Override
    public Iterable<ChannelGroupConfiguration> getChannelGroups() {
        return this.connector.getChannelGroups();
    }

    @Override
    public MessagingChannel connect(String channelGroup) {
        if (this.currentToken.get() < 0)
            throw new ObjectDisposedException(this.getClass().getName());
        if (! this.available.containsKey(channelGroup))
            return this.connector.connect(channelGroup);
        while (true) {
            MessagingChannel channel = this.tryConnect(channelGroup,
                    this.available.get(channelGroup));
            if (channel.getActive())
                return channel;
            channel.dispose();
        }
    }

    private MessagingChannel tryConnect(String channelGroup,
            List<MessagingChannel> items) {
        MessagingChannel channel;
        try {
            channel = items.remove(0);
        } catch (IndexOutOfBoundsException ex) {
            channel = this.connector.connect(channelGroup);
        }
        this.open.put(channel, true);
        return new PooledDispatchChannel(this, channel, this.currentToken.get());
    }

    public void release(MessagingChannel channel, int token) {
        if (channel == null)
            throw new NullPointerException("channel must not be null");
        if (this.open.remove(channel) == null)
            throw new IllegalStateException("Cannot release a channel that " +
                    "didn't originate with this connector.");
        int currentToken = this.currentToken.get();
        if (currentToken >= 0 && currentToken == token && channel.getActive())
            this.available.get(channel.getCurrentConfiguration().getGroupName())
                    .add(channel);
        else
            tryDispose(channel, false);
    }

    public void teardown(MessagingChannel channel, int token) {
        if (channel == null)
            throw new NullPointerException("channel must not be null");
        if (!this.open.containsKey(channel))
            throw new IllegalStateException("Cannot tear down a channel that " +
                    "didn't originate with this connector.");
        if (this.firstOneThrough(token, token + 1))
            this.clearAvailableChannels();
        tryDispose(channel, false);
    }

    private boolean firstOneThrough(int token, int assignment) {
        return this.currentToken.compareAndSet(token, assignment);
    }

    protected void clearAvailableChannels() {
        for (List<MessagingChannel> collection: this.available.values()) {
            while (true) {
                try {
                    MessagingChannel disconnected = collection.remove(0);
                    tryDispose(disconnected, false);
                } catch (IndexOutOfBoundsException ex) {
                    return;
                }
            }
        }
    }

    @Override
    public void dispose() {
        int currentToken = this.currentToken.get();
        if (currentToken < 0 || !this.firstOneThrough(currentToken, disposed))
            return;
        this.clearAvailableChannels();
        tryDispose(this.connector, false);
    }
}
