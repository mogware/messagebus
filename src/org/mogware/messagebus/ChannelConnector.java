package org.mogware.messagebus;

import org.mogware.system.Disposable;

/**
* Provides the ability to open, establish, and maintain a connection to the
* messaging infrastructure.
*
* Instances of this class must be designed to be multi-thread safe such that
* they can be shared between threads.
*/

public interface ChannelConnector extends Disposable {
    /**
    * Gets a value indicating the current state underlying connection.
    */
    ConnectionState getCurrentState();

    /**
    * Gets the set of values which uniquely identify the channel groups to
    * be created.
    */
    Iterable<ChannelGroupConfiguration> getChannelGroups();

    /**
    * Opens a channel against the underlying connection.
    */
    MessagingChannel connect(String channelGroup);
}
