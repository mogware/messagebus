package org.mogware.messagebus.channels;

import org.mogware.messagebus.ChannelConnector;
import org.mogware.messagebus.ChannelGroupConfiguration;
import org.mogware.messagebus.ConnectionState;
import org.mogware.messagebus.DependencyResolver;
import org.mogware.messagebus.ExtensionMethods;
import static org.mogware.messagebus.ExtensionMethods.tryDispose;
import org.mogware.messagebus.MessagingChannel;

public class DependencyResolverConnector implements ChannelConnector {
    private final ChannelConnector connector;

    public DependencyResolverConnector(ChannelConnector connector) {
        if (connector == null)
            throw new NullPointerException("connector must not be null");
        this.connector = connector;
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
        MessagingChannel channel = this.connector.connect(channelGroup);
        DependencyResolver resolver =
                channel.getCurrentConfiguration().getDependencyResolver();
        if (resolver == null)
            return channel;
        try {
            return new DependencyResolverChannel(channel,
                    resolver.createNestedResolver());
        } catch (Exception ex) {
            tryDispose(resolver, false);
            throw ex;
        }
    }

    @Override
    public void dispose() {
        tryDispose(this.connector, false);
    }
}
