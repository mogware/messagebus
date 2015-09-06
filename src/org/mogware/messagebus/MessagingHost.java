package org.mogware.messagebus;

import org.mogware.system.Disposable;
import org.mogware.system.delegates.Action1;

/**
* Represents the primary, high-level interface for working with sending and
* receiving messages.
* 
* Instances of this class must be designed to be multi-thread safe such that
* they can be shared between threads.
*/

public interface MessagingHost extends Disposable {
    /**
    * Creates all channel groups and the initializes each of them and returns
    * the first one.
    */
    ChannelGroup initialize();

    /**
    * Begins streaming any available inbound messages to the callback provided.
    */
    void beginReceive(Action1<DeliveryContext> callback);

    /**
    * Obtains a reference to the channel group for the key specified.
    */
    ChannelGroup getChannelGroup(String channelGroup);
}
