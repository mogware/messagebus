package org.mogware.messagebus;

/**
* Provides the ability to construct a new channel group.
*/

@FunctionalInterface
public interface ChannelGroupFactory {
    ChannelGroup invoke(ChannelConnector connector,
            ChannelGroupConfiguration configuration);    
}
